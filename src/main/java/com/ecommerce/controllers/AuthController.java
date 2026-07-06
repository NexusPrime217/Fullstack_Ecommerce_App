package com.ecommerce.controllers;

import com.ecommerce.models.AppRoles;
import com.ecommerce.models.Role;
import com.ecommerce.models.User;
import com.ecommerce.repositories.RoleRepository;
import com.ecommerce.security.jwt.JwtUtils;
import com.ecommerce.security.repositories.UserRepository;
import com.ecommerce.security.request.LoginRequest;
import com.ecommerce.security.request.SignupRequest;
import com.ecommerce.security.response.MessageReponse;
import com.ecommerce.security.response.UserInfoResponse;
import com.ecommerce.security.services.UserDetailImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final RoleRepository roleRepository;

        @Autowired
        public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
                this.jwtUtils = jwtUtils;
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.roleRepository = roleRepository;
        }

        @PostMapping("/signin")
        public ResponseEntity<?> authenticationUser(@RequestBody LoginRequest loginRequest){
                System.out.println(System.getenv("JWTSECRET"));
                System.out.println(System.getenv("JWTSECRET").length());
                Authentication authentication;
                try{
                        authentication=authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
                }catch(AuthenticationException exception){
                        Map<String,Object> map=new HashMap<>();
                        map.put("Message","Bad Credentials");
                        map.put("Status",false);
                        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);

                UserDetailImpl userDetails=(UserDetailImpl) authentication.getPrincipal();

                ResponseCookie jwtCookie=jwtUtils.getJwtCookie(userDetails);

                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .toList();

                UserInfoResponse userInfoResponse =new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                        .body(userInfoResponse);
        }


        @PostMapping("/signup")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
                if (userRepository.existsByUsername(signupRequest.getUsername())){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageReponse("Error: Username already exists!"));
                }

                if (userRepository.existsByEmail(signupRequest.getEmail())){
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageReponse("Error: Email has been taken already!"));
                }

                User user= new User(
                        signupRequest.getUsername(),
                        signupRequest.getEmail(),
                        passwordEncoder.encode(signupRequest.getPassword())
                );

                Set<String> strRoles=signupRequest.getRole();
                Set<Role> roles=new HashSet<>();

                if (strRoles.isEmpty()){
                        Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }else{
                        strRoles.forEach(role->{
                                switch (role){
                                        case "admin":
                                                Role adminRole = roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                                                        .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                                                roles.add(adminRole);
                                                break;
                                        case "seller":
                                                Role sellerRole = roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
                                                        .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                                                roles.add(sellerRole);
                                                break;
                                        default:
                                                Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                                                        .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                                                roles.add(userRole);
                                                break;
                                }
                        });
                }

                user.setRoles(roles);
                userRepository.save(user);
                return ResponseEntity.ok().body(new MessageReponse("User created successfully"));
        }

        @GetMapping("/username")
        public String currentUsername(Authentication authentication){
                if (authentication!=null){
                        return authentication.getName();
                } else{
                        return "";
                }
        }

        @GetMapping("/user")
        public ResponseEntity<UserInfoResponse> getuserDetails(Authentication authentication){

                UserDetailImpl userDetail = (UserDetailImpl) authentication.getPrincipal();
                List<String> roles = userDetail.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .toList();

                UserInfoResponse userInfoResponse = new UserInfoResponse(userDetail.getId(), userDetail.getUsername(), roles);
                return ResponseEntity.ok()
                        .body(userInfoResponse);

        }

        @PostMapping("/signout")
        public ResponseEntity<?> signoutUser(){
                ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE,cookie.toString())
                        .body(new MessageReponse("You've been signed out!"));
        }

}

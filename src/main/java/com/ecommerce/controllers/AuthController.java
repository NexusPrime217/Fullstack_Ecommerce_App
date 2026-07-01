package com.ecommerce.controllers;

import com.ecommerce.models.AppRoles;
import com.ecommerce.models.Role;
import com.ecommerce.models.User;
import com.ecommerce.repositories.roleRepository;
import com.ecommerce.security.jwt.JwtUtils;
import com.ecommerce.security.repositories.UserRepository;
import com.ecommerce.security.request.LoginRequest;
import com.ecommerce.security.request.SignupRequest;
import com.ecommerce.security.response.MessageReponse;
import com.ecommerce.security.response.UserInfoResponse;
import com.ecommerce.security.services.UserDetailImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

public class AuthController {

        private JwtUtils jwtUtils;
        private AuthenticationManager authenticationManager;
        private UserRepository userRepository;
        private PasswordEncoder passwordEncoder;
        private roleRepository roleRepository;


        @PostMapping("/signin")
        public ResponseEntity<?> authenticationUser(@RequestBody LoginRequest loginRequest){
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

                String jwtToken=jwtUtils.getJWTFromUsername(userDetails);

                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .toList();

                UserInfoResponse userInfoResponse =new UserInfoResponse(userDetails.getId(), jwtToken, userDetails.getUsername(), roles);
                return new ResponseEntity<>(userInfoResponse,HttpStatus.OK);
        }


        @PostMapping("/signup")
        public  ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
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

                if (strRoles==null){
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
}

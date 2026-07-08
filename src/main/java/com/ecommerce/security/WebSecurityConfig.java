package com.ecommerce.security;

import com.ecommerce.models.AppRoles;
import com.ecommerce.models.Role;
import com.ecommerce.models.User;
import com.ecommerce.repositories.RoleRepository;
import com.ecommerce.security.jwt.AuthEntryPointJwt;
import com.ecommerce.security.jwt.AuthTokenFilter;
import com.ecommerce.security.jwt.JwtUtils;
import com.ecommerce.security.repositories.UserRepository;
import com.ecommerce.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt authEntryPointJwt;
    private final JwtUtils jwtUtils;

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt authEntryPointJwt,JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.authEntryPointJwt = authEntryPointJwt;
        this.jwtUtils=jwtUtils;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter(jwtUtils,userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=  new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig){
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests((auth)->
                          auth.requestMatchers("/api/auth/signin").permitAll()
                                  .requestMatchers("/api/auth/signout").permitAll()
                                  .requestMatchers("/api/auth/signup").permitAll()
                                  .requestMatchers("/api/auth/signin").permitAll()
                                  .requestMatchers("/v3/api-docs/**").permitAll()
                                  .requestMatchers("/h2-console/**").permitAll()
                                  .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                                  .requestMatchers("/api/public/**").permitAll()
                         .anyRequest().authenticated()
                )
                .exceptionHandling(exception->exception.authenticationEntryPoint(authEntryPointJwt))
                .csrf(csrf->csrf.disable())
                .headers(header-> header.frameOptions(frame-> frame.sameOrigin()))
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(withDefaults());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
//        http.formLogin(withDefaults());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring().requestMatchers(
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"
        ));
    }

//    @Bean
//    public CommandLineRunner dataInit(RoleRepository roleRepository, UserRepository userRepository,PasswordEncoder passwordEncoder){
//        return args -> {
//            Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
//                    .orElseGet(()->{
//                        Role newUserRole=new Role(AppRoles.ROLE_USER);
//                        return roleRepository.save(newUserRole);
//                    });
//            Role sellerRole=roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
//                    .orElseGet(()->{
//                       Role newSellerRole=new Role(AppRoles.ROLE_SELLER);
//                       return roleRepository.save(newSellerRole);
//                    });
//            Role adminRole=roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
//                    .orElseGet(()->{
//                        Role newAdminRole=new Role(AppRoles.ROLE_ADMIN);
//                        return roleRepository.save(newAdminRole);
//                    });
//
//            Set<Role> userRoles=Set.of(userRole);
//            Set<Role> sellerRoles=Set.of(sellerRole);
//            Set<Role> adminRoles=Set.of(adminRole);
//
//            //Create users if not already present
//            if (!userRepository.existsByUsername("user1")){
//                User user1= new User("user1", "user1@gmail.com",passwordEncoder.encode("password123"));
//                userRepository.save(user1);
//            }
//            if (!userRepository.existsByUsername("seller1")){
//                User seller1= new User("seller1", "seller1@gmail.com",passwordEncoder.encode("pass123"));
//                userRepository.save(seller1);
//            }
//            if (!userRepository.existsByUsername("admin1")){
//                User admin1= new User("admin1", "admin@gmail.com",passwordEncoder.encode("pass123"));
//                userRepository.save(admin1);
//            }
//
//            //Update roles for existing users
//            userRepository.findByUsername("user1").ifPresent(user -> {
//                user.setRoles(userRoles);
//                userRepository.save(user);
//            });
//
//            userRepository.findByUsername("seller1").ifPresent(seller -> {
//                seller.setRoles(sellerRoles);
//                userRepository.save(seller);
//            });
//
//            userRepository.findByUsername("admin1").ifPresent(admin -> {
//                admin.setRoles(adminRoles);
//                userRepository.save(admin);
//            });
//
//        };
//    }
}

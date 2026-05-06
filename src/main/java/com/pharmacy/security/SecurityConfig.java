package com.pharmacy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthFailureHandler failureHandler;
    private final AuthSuccessHandler successHandler;
    private final RecaptchaValidationFilter recaptchaValidationFilter;

    public SecurityConfig(UserDetailsService userDetailsService, AuthFailureHandler failureHandler,
                          AuthSuccessHandler successHandler,
                          RecaptchaValidationFilter recaptchaValidationFilter) {
        this.userDetailsService = userDetailsService;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
        this.recaptchaValidationFilter = recaptchaValidationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureHandler(failureHandler)
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .addFilterBefore(recaptchaValidationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

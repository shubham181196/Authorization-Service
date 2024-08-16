package com.example.AuthorizationService.config;

import com.example.AuthorizationService.Filters.JwtFilter;
import com.example.AuthorizationService.Interceptor.sampleInterceptor;
import com.example.AuthorizationService.Services.userDetailsServiceImpl;
import com.example.AuthorizationService.helper.CustomAuthenticationError;
import com.example.AuthorizationService.helper.customAccesDenied;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class springSecurity implements WebMvcConfigurer{


    private UserDetailsService userDetailsService;
    private JwtFilter jwtFilter;
    private sampleInterceptor sampleinterceptor;
    private successHandler successHandler;

    public springSecurity(JwtFilter jwtFilter,userDetailsServiceImpl userDetailsService,sampleInterceptor sampleinterceptor,successHandler successHandler){
        this.userDetailsService=userDetailsService;
        this.jwtFilter=jwtFilter;
        this.sampleinterceptor=sampleinterceptor;
        this.successHandler=successHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            return http.csrf(csrf->csrf.disable())
                    .cors(cors->cors.disable())
                    .authorizeHttpRequests(
                            authorize-> authorize
                                    .requestMatchers("/auth/**","/login/**").permitAll()
                                    .requestMatchers("/resource/**").hasRole("ADMIN")

                                    .anyRequest().authenticated()
                    ).exceptionHandling((exception)->
                            exception.authenticationEntryPoint(new CustomAuthenticationError())
                                      .accessDeniedHandler(new customAccesDenied())

                    )
                    .authenticationProvider(authenticationProvider())
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .oauth2Login(oauth2-> {
                                oauth2.loginPage("http://localhost:3000");
                                oauth2.successHandler(successHandler);
                            }
                    )
                    .build();


    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(encoderGenerator());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder encoderGenerator(){
        return new BCryptPasswordEncoder();
    }
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**").allowCredentials(true).allowedOriginPatterns("*").allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sampleinterceptor); 
    }
}

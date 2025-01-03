package com.example.AuthorizationService.config;

import com.example.AuthorizationService.DTO.loginRequestDTO;
import com.example.AuthorizationService.Services.JwtService;
import com.example.AuthorizationService.Services.userDetailsServiceImpl;
import com.example.AuthorizationService.Services.userService;
import com.example.CentralRepository.models.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.IOException;
import java.util.Date;

@Component
public class successHandler implements AuthenticationSuccessHandler {

    private  JwtService jwtService;

    private userDetailsServiceImpl userDetailsService;
    @Value("${cookie.expiry}")
    private int cookieExpiry;

    private userService userservice;
    public successHandler(JwtService jwtService,userDetailsServiceImpl userDetailsService,userService userservice) {
        this.userservice=userservice;
        this.userDetailsService=userDetailsService;
        this.jwtService=jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User defaultOAuth2User= (DefaultOAuth2User) authentication.getPrincipal();
        System.out.println("principal: " +defaultOAuth2User.getAttributes().toString());
        System.out.println("DDDDDDD: " +defaultOAuth2User.getAttributes().get("email"));
        Users user=userservice.findUserByEmail(defaultOAuth2User.getAttributes().get("email").toString());
        if(user==null){
            loginRequestDTO loginrequestDTO= loginRequestDTO.builder()
                    .pass("123abc")
                    .email(defaultOAuth2User.getAttributes().get("email").toString())
                    .phone("0123456789")
                    .build();
            Users user1=userservice.saveUser(loginrequestDTO);
        }
        String token=jwtService.createToken(defaultOAuth2User.getAttributes().get("email").toString());


        System.out.println(token);
        ResponseCookie cookie=ResponseCookie.from("Jwttoken",token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(cookieExpiry)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE,cookie.toString());

       new DefaultRedirectStrategy().sendRedirect(request,response,"http://localhost:3000/login/home");

    }
}

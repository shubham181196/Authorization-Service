package com.example.AuthorizationService.Filters;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SampleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request=(HttpServletRequest) servletRequest;
        String s= String.valueOf(request.getRequestURL());
        System.out.println(s);
        filterChain.doFilter(request,servletResponse);
        HttpServletResponse response=(HttpServletResponse) servletResponse;




    }
}

package com.example.AuthorizationService.Filters;

import com.example.AuthorizationService.Services.JwtService;
import com.example.AuthorizationService.Services.userDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter  {
    private final int c=0;
    private userDetailsServiceImpl userdetailsService;
    private JwtService jwtService;
    private final RequestMatcher uriMatcher=new AntPathRequestMatcher("/resource/**", HttpMethod.POST.name());
    public JwtFilter(userDetailsServiceImpl userdetailsService,JwtService jwtService){
        this.userdetailsService=userdetailsService;
        this.jwtService=jwtService;

    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token ="";
        log.info("Inside JWT filter");
        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if(cookie.getName().equals("Jwttoken")){
                    token=cookie.getValue();
                }
            }
        }
        if(token.equals("")){
            // user has not provided any jwt token hence request should not move forward
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"incorrect credentials\"}");
            return;
        }

        String email =jwtService.extractEmail(token);
        if(email!=null){
            UserDetails userDetails=userdetailsService.loadUserByUsername(email);
            if(jwtService.validateToken(token,userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(request,response);

            }
        }else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"incorrect credentials\"}");
            return;
        }
        filterChain.doFilter(request,response);

    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        RequestMatcher matcher=new NegatedRequestMatcher(uriMatcher);
         return matcher.matches(request);

    }
}

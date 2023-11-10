package com.programmingcodez.userservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();



        if ("/addUser".equals(path)) {
            filterChain.doFilter(request, response);
        } else {
            // For all other routes, require authentication

            jwtTokenInterceptor.preHandle(request, response, null);
            filterChain.doFilter(request, response);
        }
    }
}

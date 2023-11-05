package com.programmingcodez.userservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtTokenInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtTokenInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Get the JWT token from the request header
        String token = request.getHeader("Authorization");

        // Validate the token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove the "Bearer " prefix
            try {
                //Verify the token and extract the claims
                Claims claims = jwtUtil.extractAllClaims(token);
                return true;
//        }
            } catch (ExpiredJwtException ex) {
                // Token has expired, handle it appropriately
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            } catch (Exception ex) {
                // Token validation failed, handle it appropriately
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }


        }
        // Token is invalid or not present, return an unauthorized response
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}

package com.programmingcodez.userservice.jwt;

import com.programmingcodez.userservice.dto.LoginInfo;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtUtil {
    private final UserService userService;
    @Autowired
    public JwtUtil(UserService userService){this.userService=userService;}

    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(LoginInfo loginInfo) {
        Optional<User> usr = userService.getUser(loginInfo.getUserName());
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName",usr.get().getUserName());
        claims.put("role",usr.get().isCus());
        return createToken(claims);
    }
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // token will expire after 10 hours
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }



}

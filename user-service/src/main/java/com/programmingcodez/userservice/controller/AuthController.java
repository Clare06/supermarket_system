package com.programmingcodez.userservice.controller;

import com.programmingcodez.userservice.dto.LoginInfo;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.jwt.JwtUtil;
import com.programmingcodez.userservice.repository.UserRepository;
import com.programmingcodez.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@CrossOrigin(allowedHeaders = "*",origins = "*")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody LoginInfo loginInfo) {

        if (this.userService.userAuth(loginInfo)){
            String token = jwtUtil.generateToken(loginInfo);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("User Invalid");
    }


}

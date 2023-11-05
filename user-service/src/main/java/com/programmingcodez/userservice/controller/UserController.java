package com.programmingcodez.userservice.controller;

import com.programmingcodez.userservice.dto.LoginInfo;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.jwt.JwtUtil;
import com.programmingcodez.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@CrossOrigin(allowedHeaders = "*",origins = "*")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAll(){
        return new ResponseEntity<>(this.userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/getUser/{userName}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable String userName){
        return new ResponseEntity<>(this.userService.getUser(userName), HttpStatus.OK);
    }

    @GetMapping("/checkUser/{userName}")
    public ResponseEntity<Boolean> checkUser(@PathVariable String userName){
        return new ResponseEntity<>(this.userService.checkUser(userName), HttpStatus.OK);
    }

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User user){
        return new ResponseEntity<>(this.userService.addUser(user), HttpStatus.OK);
    }

    @PutMapping("/editUser")
    public ResponseEntity<User> editUser(@RequestBody User user){
        return new ResponseEntity<>(this.userService.editUser(user), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{userName}")
    public ResponseEntity<String> deleteUser(@PathVariable String userName){
        return new ResponseEntity<>(this.userService.deleteUser(userName), HttpStatus.OK);
    }

    @PostMapping("authenticate")
    public ResponseEntity<String> authenticate(@RequestBody LoginInfo loginInfo) {

        if (this.userService.userAuth(loginInfo)){
            String token = jwtUtil.generateToken(loginInfo);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
}

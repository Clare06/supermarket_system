package com.programmingcodez.userservice.controller;

import com.programmingcodez.userservice.Exceptions.UsernameDuplicationException;
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
@RequestMapping("api/user")
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
        Boolean result=this.userService.checkUser(userName);
        return new ResponseEntity<>(this.userService.checkUser(userName), HttpStatus.OK);
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            User addedUser = userService.addUser(user);
            return ResponseEntity.ok(addedUser);
        } catch (UsernameDuplicationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    @PutMapping("/editUser")
    public ResponseEntity<User> editUser(@RequestBody User user){
        return new ResponseEntity<>(this.userService.editUser(user), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{userName}")
    public ResponseEntity<String> deleteUser(@PathVariable String userName){
        return new ResponseEntity<>(this.userService.deleteUser(userName), HttpStatus.OK);
    }



}

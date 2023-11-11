package com.programmingcodez.userservice.service;

import com.programmingcodez.userservice.Exceptions.UserNotFoundException;
import com.programmingcodez.userservice.Exceptions.UsernameDuplicationException;
import com.programmingcodez.userservice.dto.LoginInfo;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private UsernameDuplicationException usernameDuplicationException;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return this.userRepository.findAll();
    }

    @Override
    public Optional<User> getUser(String userName) {
        return this.userRepository.findById(userName);
    }

    @Override
    public Boolean checkUser(String userName) {
        return this.userRepository.existsById(userName);
    }

    @Override
    public User addUser(User user) {

            if (checkUser(user.getUserName())) {
                throw new UsernameDuplicationException("Username is already taken: " + user.getUserName());
            } else {
                user.setPassword(bcryptPassword(user.getPassword()));
                return this.userRepository.save(user);
            }


    }

    @Override
    public User editUser(User user) {
        if (checkUser(user.getUserName())){
            Optional<User> temp = getUser(user.getUserName());
            user.setCus(temp.get().isCus());
            return this.userRepository.save(user);
        }
        else {
            throw new UserNotFoundException("Username "+user.getUserName()+" is Not Found ");
        }
    }

    @Override
    public String deleteUser(String userName) {
        if(checkUser(userName)){
            this.userRepository.deleteById(userName);
            return "User "+ userName +" Deleted";
        }
        else {
            throw new UserNotFoundException("Username "+userName+" is Not Found ");
        }
    }

    @Override
    public boolean userAuth(LoginInfo loginInfo) {
        Optional<User> user= this.userRepository.findById(loginInfo.getUserName());
        if (checkUser(loginInfo.getUserName())){
            return bcryptMatch(loginInfo.getPassword(),user.get().getPassword());
        }
        return false;
    }
    public String bcryptPassword(String password){
        int strength = 10; // work factor of bcrypt
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        return encodedPassword;
    }
    public boolean bcryptMatch(String usrEntered, String dbPass) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return  encoder.matches(usrEntered,dbPass);
    }
    public void updatePassword(User user, String newPassword) {
        // Update the user's password
        String encodedPassword=this.bcryptPassword(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}

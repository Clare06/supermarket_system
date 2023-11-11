package com.programmingcodez.userservice.Exceptions;

public class UsernameDuplicationException extends RuntimeException {
    public UsernameDuplicationException(String message) {
        super(message);
    }

}

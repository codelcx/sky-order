package com.sky.exception;

public class UserNotExistsException extends BaseException {

    public UserNotExistsException() {
    }

    public UserNotExistsException(String message) {
        super(message);
    }

}

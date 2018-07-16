package com.kakaopay.todolist.exception;

public class InvalidTodoReqeustException extends RuntimeException {
    public InvalidTodoReqeustException () {
        super();
    }

    public InvalidTodoReqeustException (String message) {
        super(message);
    }
}

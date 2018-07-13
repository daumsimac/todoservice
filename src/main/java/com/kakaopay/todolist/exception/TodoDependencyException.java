package com.kakaopay.todolist.exception;

public class TodoDependencyException extends RuntimeException {
    public TodoDependencyException () {

    }

    public TodoDependencyException (String message) {
        super(message);
    }
}

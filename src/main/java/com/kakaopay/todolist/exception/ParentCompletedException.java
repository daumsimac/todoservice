package com.kakaopay.todolist.exception;

public class ParentCompletedException extends RuntimeException {
    public ParentCompletedException () {
        super();
    }

    public ParentCompletedException (String message) {
        super(message);
    }
}

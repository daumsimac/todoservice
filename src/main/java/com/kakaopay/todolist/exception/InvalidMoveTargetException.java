package com.kakaopay.todolist.exception;

public class InvalidMoveTargetException extends RuntimeException {
    public InvalidMoveTargetException () {
        super();
    }

    public InvalidMoveTargetException (String message) {
        super(message);
    }
}

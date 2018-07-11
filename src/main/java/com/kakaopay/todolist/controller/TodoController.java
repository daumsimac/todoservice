package com.kakaopay.todolist.controller;

import com.kakaopay.todolist.dto.SearchOption;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.awt.print.Pageable;

@RestController
public class TodoController {

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @PostMapping(
            value = "/api/v1/todo",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE },
            consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public ResponseEntity<?> createTodo () {
        // TODO : Implement me
        return null;
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @PutMapping(
            value = "/api/v1/todo/{id}",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE },
            consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public ResponseEntity<?> updateTodo () {
        // TODO : Implement me
        return null;
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @DeleteMapping(
            value = "/api/v1/todo/{id}",
            consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public ResponseEntity<?> deleteTodo () {
        // TODO : Implement me
        return null;
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @GetMapping(
            value = "/api/v1/todos",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public ResponseEntity<?> getTodos (SearchOption searchOption, Pageable pageable) {
        // TODO : Implement me
        return null;
    }
}

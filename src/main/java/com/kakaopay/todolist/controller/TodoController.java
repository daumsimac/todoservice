package com.kakaopay.todolist.controller;

import com.kakaopay.todolist.dto.SearchOption;
import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.entity.Todo;
import com.kakaopay.todolist.service.TodoService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
public class TodoController {

    @Autowired
    private TodoService todoService;

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @PostMapping(
            value = "/api/v1/todo",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE },
            consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public Todo createTodo (@RequestBody TodoDTO todoDTO) throws Exception {
        return todoService.create(todoDTO);
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

    @GetMapping(
            value = "/api/v1/todo/{id}",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public Todo getTodo (@PathVariable("id") int id) throws Exception {
        return todoService.get(id);
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

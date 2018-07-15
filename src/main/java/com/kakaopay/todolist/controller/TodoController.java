package com.kakaopay.todolist.controller;

import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.entity.Todo;
import com.kakaopay.todolist.service.TodoService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @PostMapping(
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE },
            consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public TodoDTO.CreateResponse createTodo (@RequestBody TodoDTO.CreateRequest todoDTO) {
        return todoService.create(todoDTO);
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @PutMapping(
            value = "/{id}",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE },
            consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public TodoDTO.UpdateResponse updateTodo (@PathVariable("id") int id, @RequestBody TodoDTO.UpdateRequest todoDTO) {
        return todoService.update(id, todoDTO);
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @DeleteMapping(
            value = "/{id}",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public TodoDTO.DeleteResponse deleteTodo (@PathVariable("id") int id) {
        return todoService.delete(id);
    }

    @PostMapping(
            value = "/{id}/complete",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public TodoDTO.UpdateResponse complete (@PathVariable("id") int id) {
        return todoService.complete(id);
    }

    @GetMapping(
            value = "/{id}",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public TodoDTO.FindOneResponse getTodo (@PathVariable("id") int id) {
        return todoService.get(id);
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name="test", dataType = "string", paramType = "query", value="Test")
    )
    @GetMapping(
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }
    )
    public Page<TodoDTO.FindAllResponse> getTodos (Pageable pageable) {
        log.info("TEST : " + pageable);
        return todoService.findAll(pageable);
    }
}

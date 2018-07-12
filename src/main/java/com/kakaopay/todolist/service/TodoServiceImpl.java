package com.kakaopay.todolist.service;

import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.entity.Todo;
import com.kakaopay.todolist.exception.ContentNotFoundException;
import com.kakaopay.todolist.repository.TodoRepository;
import com.kakaopay.todolist.repository.TreePathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TreePathRepository treePathRepository;

    @Transactional
    public Todo create (TodoDTO todoDTO) throws Exception {
        Date currentDate = Calendar.getInstance().getTime();

        Todo todo = new Todo();

        todo.setTodo(todoDTO.getTodo());
        todo.setUpdatedAt(currentDate);
        todo.setCreatedAt(currentDate);

        todoRepository.save(todo);

        if (todoDTO.getParentId() == null) {
            todoDTO.setParentId(todo.getId());
        }
        else {
            Optional<Todo> todoOptional = todoRepository.findById(todoDTO.getParentId());
            if (todoOptional.isPresent() == false) {
                throw new ContentNotFoundException("Couldn't find parent TODO(" + todoDTO.getParentId() + ")");
            }
        }

        treePathRepository.createTreePathByRootIdAndLeafId(todoDTO.getParentId(), todo.getId());

        return todo;
    }

    public void update (TodoDTO todo) {

    }

    public void delete (int id) {

    }

    public Todo get (int id) throws Exception {
        Optional<Todo> todo = todoRepository.findById(id);

        if (todo.isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find TODO(" + id + ")");
        }

        return todo.get();
    }
}

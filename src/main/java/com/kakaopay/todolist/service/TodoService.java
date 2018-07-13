package com.kakaopay.todolist.service;

import com.kakaopay.todolist.dto.ApiResponse;
import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.entity.Todo;

public interface TodoService {
    Todo create (TodoDTO todoDTO);

    Todo delete (int id);

    Todo update (TodoDTO todoDto);

    Todo get (int id);
}

package com.kakaopay.todolist.service;

import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.entity.Todo;

public interface TodoService {
    Todo create (TodoDTO todoDTO) throws Exception;

    void delete (int id);

    void update (TodoDTO todoDto);

    Todo get (int id) throws Exception;
}

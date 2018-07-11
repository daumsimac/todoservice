package com.kakaopay.todolist.service;

import com.kakaopay.todolist.dto.TodoDTO;

public interface TodoService {
    void create (TodoDTO todo);

    void delete (int id);

    void update (TodoDTO todo);
}

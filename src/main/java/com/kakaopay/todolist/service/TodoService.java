package com.kakaopay.todolist.service;

import com.kakaopay.todolist.dto.TodoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TodoService {
    TodoDTO.CreateResponse create (TodoDTO.CreateRequest todoDTO);

    TodoDTO.DeleteResponse delete (int id);

    TodoDTO.UpdateResponse update (int id, TodoDTO.UpdateRequest todoDto);

    TodoDTO.FindOneResponse get (int id);

    TodoDTO.UpdateResponse complete (int id);

    Page<TodoDTO.FindAllResponse> findAll (Pageable pageable);
}

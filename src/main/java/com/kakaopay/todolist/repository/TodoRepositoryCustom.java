package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TodoRepositoryCustom {
    long countByNotCompletedDescendants (int todoId);

    List<Todo> getDescendants (int todoId);

    String createDisplayContent (int todoId);
}

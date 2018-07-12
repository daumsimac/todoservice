package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    Optional<Todo> findById (Integer id);
}

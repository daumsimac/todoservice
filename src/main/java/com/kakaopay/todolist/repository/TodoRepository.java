package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer>, TodoRepositoryCustom {
    Optional<Todo> findById (int id);

    List<Todo> findByIdIn (List<Integer> ids);

    void deleteByIdIn (List<Integer> ids);
}

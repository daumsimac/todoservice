package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.TreePath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreePathRepository extends JpaRepository<TreePath, Integer>, TreePathRepositoryCustom {
}

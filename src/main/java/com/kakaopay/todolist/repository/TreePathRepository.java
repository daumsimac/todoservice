package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.TreePath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreePathRepository extends JpaRepository<TreePath, Integer>, TreePathRepositoryCustom {
    List<TreePath> findByAncestor (int ancestor);

    void deleteByDescendantIn (List<Integer> descendantList);
}

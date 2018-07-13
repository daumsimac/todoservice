package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.TreePath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TreePathRepository extends JpaRepository<TreePath, Integer>, TreePathRepositoryCustom {
    List<TreePath> findByAncestor (int ancestor);

    void deleteByDescendantIn (List<Integer> descendantList);

    void deleteByDescendantInAndAncestorIn (List<Integer> descendantList, List<Integer> ancestorList);
}

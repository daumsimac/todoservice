package com.kakaopay.todolist.service;

import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.entity.Todo;
import com.kakaopay.todolist.entity.TreePath;
import com.kakaopay.todolist.exception.ContentNotFoundException;
import com.kakaopay.todolist.exception.InvalidMoveTargetException;
import com.kakaopay.todolist.repository.TodoRepository;
import com.kakaopay.todolist.repository.TreePathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TreePathRepository treePathRepository;

    @Transactional
    public Todo create (TodoDTO todoDTO) {
        if (todoDTO.getParentId() != null && getTodo(todoDTO.getParentId()).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find parent TODO(" + todoDTO.getParentId() + ")");
        }

        Date currentDate = Calendar.getInstance().getTime();

        Todo todo = new Todo();

        todo.setTodo(todoDTO.getTodo());
        todo.setUpdatedAt(currentDate);
        todo.setCreatedAt(currentDate);

        todoRepository.save(todo);

        int parentId = todo.getId();
        if (todoDTO.getParentId() != null) {
            parentId = todoDTO.getParentId();
        }

        treePathRepository.createTreePathByRootIdAndLeafId(parentId, todo.getId());

        return todo;
    }

    @Transactional
    public Todo update (TodoDTO todoDTO) {
        Optional<Todo> optionalTodo = null;
        if ((optionalTodo = getTodo(todoDTO.getId())).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find TODO(" + todoDTO.getId() + ")");
        }

        Todo todo = optionalTodo.get();

        if (todoDTO.getTodo() != null) {
            todo.setTodo(todoDTO.getTodo());
        }

        todoRepository.save(todo);

        todo.setUpdatedAt(Calendar.getInstance().getTime());

        if (todoDTO.getParentId() != null) {
            if (getTodo(todoDTO.getParentId()).isPresent() == false) {
                throw new ContentNotFoundException("Couldn't find TODO(" + todoDTO.getParentId() + ")");
            }

            if (!movable(todoDTO.getId(), todoDTO.getParentId())) {
                throw new InvalidMoveTargetException("Couldn't move TODO(" + todoDTO.getId() +
                        ") to child TODO(" + todoDTO.getParentId() + ")");
            }

            treePathRepository.moveSubTreeTo(todo.getId(), todoDTO.getParentId());
        }

        return todo;
    }

    @Transactional
    public Todo delete (int id) {
        Optional<Todo> optionalTodo = null;
        if ((optionalTodo = getTodo(id)).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find TODO(" + id + ")");
        }

        List<TreePath> treePathList = treePathRepository.findByAncestor(id);
        List<Integer> deleteIds = new ArrayList<>();

        for (TreePath  treePath : treePathList) {
            deleteIds.add(treePath.getDescendant());
        }

        treePathRepository.deleteByDescendantIn(deleteIds);
        todoRepository.deleteByIdIn(deleteIds);

        return optionalTodo.get();
    }

    @Transactional(readOnly = true)
    public Todo get (int id) {
        Optional<Todo> optionalTodo = null;
        if ((optionalTodo = getTodo(id)).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find TODO(" + id + ")");
        }

        log.info("TEST : " + optionalTodo.get());
        return optionalTodo.get();
    }

    private boolean movable (int subTreeRootId, int moveTo) {
        List<TreePath> treePaths = treePathRepository.findByAncestor(subTreeRootId);

        return (treePaths.stream().filter(treePath -> treePath.getId() == moveTo).count() == 0);
    }

    private Optional<Todo> getTodo (int id) {
        return todoRepository.findById(id);
    }
}

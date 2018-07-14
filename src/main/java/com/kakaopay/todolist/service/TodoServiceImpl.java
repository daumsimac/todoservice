package com.kakaopay.todolist.service;

import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.entity.Todo;
import com.kakaopay.todolist.entity.TreePath;
import com.kakaopay.todolist.exception.ContentNotFoundException;
import com.kakaopay.todolist.exception.InvalidMoveTargetException;
import com.kakaopay.todolist.exception.TodoDependencyException;
import com.kakaopay.todolist.repository.TodoRepository;
import com.kakaopay.todolist.repository.TreePathRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public TodoDTO.CreateResponse create (TodoDTO.CreateRequest todoDTO) {
        if (todoDTO.getParentId() != null && getTodo(todoDTO.getParentId()).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find parent TODO(" + todoDTO.getParentId() + ")");
        }

        Date currentDate = Calendar.getInstance().getTime();

        Todo todo = new Todo();

        todo.setContent(todoDTO.getContent());
        todo.setUpdatedAt(currentDate);
        todo.setCreatedAt(currentDate);

        todoRepository.save(todo);

        int parentId = todo.getId();
        if (todoDTO.getParentId() != null) {
            parentId = todoDTO.getParentId();
        }

        treePathRepository.createTreePathByRootIdAndLeafId(parentId, todo.getId());

        String displayContent = todoRepository.createDisplayContent(todo.getId());
        if (displayContent == null) {
            displayContent = todo.getContent();
        }
        todo.setDisplayContent(displayContent);

        return modelMapper.map(todo, TodoDTO.CreateResponse.class);
    }

    @Transactional
    public TodoDTO.UpdateResponse update (int id, TodoDTO.UpdateRequest todoDTO) {
        Optional<Todo> optionalTodo = null;
        if ((optionalTodo = getTodo(id)).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find TODO(" + id + ")");
        }

        Todo todo = optionalTodo.get();

        if (todoDTO.getContent() != null) {
            todo.setContent(todoDTO.getContent());
        }

        todo.setUpdatedAt(Calendar.getInstance().getTime());

        todoRepository.save(todo);

        if (todoDTO.getParentId() != null) {
            moveSubtree(todo, todoDTO);
        }

        return modelMapper.map(todo, TodoDTO.UpdateResponse.class);
    }

    @Transactional
    public TodoDTO.DeleteResponse delete (int id) {
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

        return modelMapper.map(optionalTodo.get(), TodoDTO.DeleteResponse.class);
    }

    @Transactional
    public TodoDTO.UpdateResponse complete (int id) {
        Optional<Todo> optionalTodo = null;
        if ((optionalTodo = getTodo(id)).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find TODO(" + id + ")");
        }

        if (completable(id) == false) {
            throw new TodoDependencyException("Couldn't complete TODO(" + id + ")");
        }

        Todo todo = optionalTodo.get();

        Date currentDate = Calendar.getInstance().getTime();

        todo.setCompletedAt(currentDate);
        todo.setUpdatedAt(currentDate);

        todoRepository.save(todo);

        return modelMapper.map(todo, TodoDTO.UpdateResponse.class);
    }

    @Transactional(readOnly = true)
    public Page<TodoDTO.FindAllResponse> findAll (Pageable pageable) {
        return todoRepository.findAll(pageable).map(
                todo -> modelMapper.map(todo, TodoDTO.FindAllResponse.class)
        );
    }

    @Transactional(readOnly = true)
    public TodoDTO.FindOneResponse get (int id) {
        Optional<Todo> optionalTodo = null;
        if ((optionalTodo = getTodo(id)).isPresent() == false) {
            throw new ContentNotFoundException("Couldn't find TODO(" + id + ")");
        }

        return modelMapper.map(optionalTodo.get(), TodoDTO.FindOneResponse.class);
    }

    private boolean movable (int subTreeRootId, int moveTo) {
        List<TreePath> treePaths = treePathRepository.findByAncestor(subTreeRootId);

        return (treePaths.stream().filter(treePath -> treePath.getId() == moveTo).count() == 0);
    }

    private Optional<Todo> getTodo (int id) {
        return todoRepository.findById(id);
    }

    private boolean completable (int todoId) {
        return (todoRepository.countByNotCompletedDescendants(todoId) == 0);
    }

    private void moveSubtree (Todo todo, TodoDTO.UpdateRequest todoDTO) {
        if (!movable(todo.getId(), todoDTO.getParentId())) {
            throw new InvalidMoveTargetException("Couldn't move TODO(" + todo.getId() +
                    ") to child TODO(" + todoDTO.getParentId() + ")");
        }

        treePathRepository.detachFromTree(todo.getId());
        treePathRepository.moveSubTreeTo(todo.getId(), todoDTO.getParentId());

        treePathRepository.findByAncestor(todo.getId()).stream().forEach(treePath -> {
            Optional<Todo> ot = getTodo(treePath.getDescendant());
            if (ot.isPresent() == false) {
                throw new ContentNotFoundException("Couldn't find TODO(" + treePath.getDescendant() + ")");
            }

            Todo td = ot.get();
            String displayContent = todoRepository.createDisplayContent(td.getId());
            if (displayContent == null) {
                displayContent = td.getContent();
            }

            td.setDisplayContent(displayContent);

            todoRepository.save(td);
        });
    }

}

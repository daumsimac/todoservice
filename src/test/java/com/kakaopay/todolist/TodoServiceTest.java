package com.kakaopay.todolist;

import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.dto.TreePathDTO;
import com.kakaopay.todolist.service.TodoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoServiceTest {
    @Autowired
    private TodoService todoService;

    private TodoDTO.CreateResponse createTodo (String content, Integer parentId) {
        TodoDTO.CreateRequest createRequest = new TodoDTO.CreateRequest();
        createRequest.setContent(content);
        if (parentId != null) {
            createRequest.setParentId(parentId);
        }

        return todoService.create(createRequest);
    }

    private TodoDTO.FindOneResponse findTodo (int id) {
        return todoService.get(id);
    }

    @Test
    public void testCreateTodo () {
        String content = "Test Todo";

        for (int i = 0; i < 100 ; i++) {
            TodoDTO.CreateResponse createResponse = createTodo(content, null);

            Assert.assertNotNull(createResponse.getId());
            Assert.assertEquals(content, createResponse.getContent());

            TodoDTO.FindOneResponse findOneResponse = findTodo(createResponse.getId());

            Assert.assertNotNull(findOneResponse);
            Assert.assertEquals(createResponse.getId(), findOneResponse.getId());
            Assert.assertEquals(createResponse.getContent(), findOneResponse.getContent());
        }
    }

    @Test
    public void testUpdateTodo () {
        String content = "Test Todo";

        TodoDTO.CreateResponse createResponse = createTodo(content, null);

        Assert.assertNotNull(createResponse.getId());
        Assert.assertEquals(content, createResponse.getContent());

        TodoDTO.FindOneResponse findOneResponse = findTodo(createResponse.getId());

        Assert.assertNotNull(findOneResponse);
        Assert.assertEquals(createResponse.getId(), findOneResponse.getId());
        Assert.assertEquals(createResponse.getContent(), findOneResponse.getContent());

        TodoDTO.UpdateRequest updateRequest = new TodoDTO.UpdateRequest();
        updateRequest.setContent("Update Test");

        TodoDTO.UpdateResponse updateResponse = todoService.update(createResponse.getId(), updateRequest);

        Assert.assertNotEquals(createResponse.getContent(), updateResponse.getContent());
    }

    @Test
    public void testPagination () {
        String content = "Test Todo";

        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < 50 ; i++) {
            TodoDTO.CreateResponse createResponse = createTodo(content, null);

            Assert.assertNotNull(createResponse.getId());
            Assert.assertEquals(content, createResponse.getContent());

            TodoDTO.FindOneResponse findOneResponse = findTodo(createResponse.getId());

            Assert.assertNotNull(findOneResponse);
            Assert.assertEquals(createResponse.getId(), findOneResponse.getId());
            Assert.assertEquals(createResponse.getContent(), findOneResponse.getContent());

            ids.add(createResponse.getId());
        }

        for (int i = 0; i < 5; i++) {
            Pageable pageable = new PageRequest(i, 10);
            Page<TodoDTO.FindAllResponse> result = todoService.findAll(pageable);
            Assert.assertEquals(10, result.getSize());
            Assert.assertEquals(10, result.getContent().size());
        }
    }

    @Test
    public void testDeleteTodo () {
        String content = "Test Todo";

        TodoDTO.CreateResponse createResponse = createTodo(content, null);

        TodoDTO.DeleteResponse deleteResponse = todoService.delete(createResponse.getId());

        Assert.assertEquals(createResponse.getId(), deleteResponse.getId());
    }

    @Test
    public void testCreateTodoWithParent () {
        String content = "Parent Todo";

        TodoDTO.CreateResponse createResponse = createTodo(content, null);

        Assert.assertNotNull(createResponse.getId());
        Assert.assertEquals(content, createResponse.getContent());

        TodoDTO.FindOneResponse findOneResponse = findTodo(createResponse.getId());

        Assert.assertNotNull(findOneResponse);
        Assert.assertEquals(findOneResponse.getId(), createResponse.getId());
        Assert.assertEquals(findOneResponse.getContent(), createResponse.getContent());

        content = "Child Todo";
        TodoDTO.CreateResponse createChildResponse = createTodo(content, createResponse.getId());

        Assert.assertEquals(content, createChildResponse.getContent());

        findOneResponse = findTodo(createChildResponse.getId());
        Assert.assertEquals(2, findOneResponse.getAncestors().size());

        String displayContent = content + " @" + createResponse.getId();
        Assert.assertEquals(displayContent, findOneResponse.getDisplayContent());
    }

    @Test
    public void testCompleteTest () {
        String content = "Todo";

        TodoDTO.CreateResponse createResponse = createTodo(content, null);

        TodoDTO.UpdateResponse updateResponse = todoService.complete(createResponse.getId());

        Assert.assertNotNull(updateResponse.getCompletedAt());

        createResponse = createTodo(content, null);
        TodoDTO.CreateResponse createChildResponse = createTodo(content, createResponse.getId());

        try {
            todoService.complete(createResponse.getId());
            Assert.assertTrue(false);
        }
        catch (Exception e) {
            Assert.assertTrue(true);
        }

        updateResponse = todoService.complete(createChildResponse.getId());
        Assert.assertNotNull(updateResponse.getCompletedAt());

        updateResponse = todoService.complete(createResponse.getId());
        Assert.assertNotNull(updateResponse.getCompletedAt());
    }

    @Test
    public void testMoveSubtree () {
        String content = "Todo";
        List<Integer> parentIds = new ArrayList<>();

        TodoDTO.CreateResponse createResponse = createTodo(content, null);
        parentIds.add(createResponse.getId());

        createResponse = createTodo(content, null);
        parentIds.add(createResponse.getId());

        createResponse = createTodo(content, parentIds.get(0));
        int childId = (createTodo(content, createResponse.getId())).getId();

        TodoDTO.FindOneResponse findOneResponse = todoService.get(createResponse.getId());

        boolean found = false;
        for (TreePathDTO treePathDTO : findOneResponse.getAncestors()) {
            if (treePathDTO.getAncestor() == parentIds.get(0)) {
                found = true;
            }
        }

        Assert.assertTrue(found);

        TodoDTO.UpdateRequest updateRequest = new TodoDTO.UpdateRequest();
        updateRequest.setParentId(parentIds.get(1));

        TodoDTO.UpdateResponse updateResponse = todoService.update(findOneResponse.getId(), updateRequest);

        Assert.assertEquals(findOneResponse.getId(), updateResponse.getId());

        findOneResponse = todoService.get(updateResponse.getId());

        Assert.assertEquals(updateResponse.getId(), findOneResponse.getId());

        found = false;
        for (TreePathDTO treePathDTO : findOneResponse.getAncestors()) {
            if (treePathDTO.getAncestor() == parentIds.get(1)) {
                found = true;
            }
        }
        Assert.assertTrue(found);

        found = false;
        for (TreePathDTO treePathDTO : findOneResponse.getDescendants()) {
            if (treePathDTO.getDescendant() == childId) {
                found = true;
            }
        }
        Assert.assertTrue(found);
    }
}

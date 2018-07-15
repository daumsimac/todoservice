package com.kakaopay.todolist;

import com.kakaopay.todolist.dto.TodoDTO;
import com.kakaopay.todolist.service.TodoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

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

    /*
     * 할일 생성 테스트
     */
    @Test
    public void testCreateTodo () {
        String content = "Test Todo";

        for (int i = 0; i < 100 ; i++) {
            TodoDTO.CreateResponse createResponse = createTodo(content, null);

            Assert.assertNotNull(createResponse.getId());
            Assert.assertEquals(content, createResponse.getContent());

            TodoDTO.FindOneResponse findOneResponse = findTodo(createResponse.getId());

            Assert.assertNotNull(findOneResponse);
            Assert.assertEquals(findOneResponse.getId(), createResponse.getId());
            Assert.assertEquals(findOneResponse.getContent(), createResponse.getContent());
        }
    }

    @Test
    public void testDeleteTodo () {
        String content = "Test Todo";

        TodoDTO.CreateResponse createResponse = createTodo(content, null);

        TodoDTO.DeleteResponse deleteResponse = todoService.delete(createResponse.getId());

        Assert.assertEquals(deleteResponse.getId(), createResponse.getId());
    }
}

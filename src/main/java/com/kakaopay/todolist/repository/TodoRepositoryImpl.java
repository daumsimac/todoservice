package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.QTodo;
import com.kakaopay.todolist.entity.QTreePath;
import com.kakaopay.todolist.entity.Todo;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Slf4j
public class TodoRepositoryImpl extends QueryDslRepositorySupport implements TodoRepositoryCustom {
    public TodoRepositoryImpl () {
        super(Todo.class);
    }

    public long countByNotCompletedDescendants (int todoId) {
        QTodo todo = QTodo.todo;
        QTreePath treePath = QTreePath.treePath;

        JPQLQuery query = from(new EntityPath[] {todo, treePath});

        query.where(treePath.ancestor.eq(todoId));
        query.where(treePath.descendant.eq(todo.id));
        query.where(treePath.descendant.ne(todoId));

        query.where(todo.completedAt.isNull());

        return (Long) query.select(todo.count()).fetchFirst();
    }

    public List<Todo> getDescendants (int todoId) {
        QTodo todo = QTodo.todo;
        QTreePath treePath = QTreePath.treePath;

        JPQLQuery query = from(new EntityPath[] {todo, treePath});

        query.where(treePath.ancestor.eq(todoId));
        query.where(treePath.descendant.eq(todo.id));
        query.where(treePath.descendant.ne(todoId));

        return query.select(todo).fetch();
    }

    public String createDisplayContent (int todoId) {
        EntityManager em = getEntityManager();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT CONCAT(td.content, ");
        sb.append("GROUP_CONCAT(CONCAT(' @', tp.ancestor) SEPARATOR ' ')) AS DISPLAY_CONTENT ");
        sb.append("FROM todos td ");
        sb.append("INNER JOIN tree_paths tp ON tp.descendant = ?1 AND tp.ancestor <> tp.descendant ");
        sb.append(" WHERE td.id = ?2 ");
        sb.append("GROUP BY td.id");

        Query query = em.createNativeQuery(sb.toString());

        query.setParameter(1, todoId);
        query.setParameter(2, todoId);

        String ret = null;
        List<Object> resultList = query.getResultList();

        if (resultList.size() > 0) {
            ret = (String) resultList.get(0);
        }

        return ret;
    }
}

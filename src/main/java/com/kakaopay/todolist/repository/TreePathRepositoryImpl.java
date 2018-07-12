package com.kakaopay.todolist.repository;

import org.qlrm.mapper.JpaResultMapper;
import com.kakaopay.todolist.entity.TreePath;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class TreePathRepositoryImpl extends QueryDslRepositorySupport implements TreePathRepositoryCustom {

    public TreePathRepositoryImpl () {
        super(TreePath.class);
    }

    public void createTreePathByRootIdAndLeafId (int rootId, int leafId) {
        EntityManager em = getEntityManager();

        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO tree_paths (ancestor, descendant) ");
        sb.append("SELECT t.ancestor, ");
        sb.append(leafId);
        sb.append("  FROM tree_paths t");
        sb.append(" WHERE t.descendant = ?1 ");
        sb.append("UNION ALL ");
        sb.append("SELECT ");
        sb.append(leafId);
        sb.append(",");
        sb.append(leafId);

        Query qry  = em.createNativeQuery(sb.toString());

        qry.setParameter(1, rootId);

        qry.executeUpdate();
    }

    public List<TreePath> getTreePathByRootIdAndLeafId (int rootId, int leafId) {
        EntityManager em = getEntityManager();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT t.ancestor, ");
        sb.append(leafId);
        sb.append("  FROM tree_paths t");
        sb.append(" WHERE t.descendant = ?1 ");
        sb.append("UNION ALL ");
        sb.append("SELECT ");
        sb.append(leafId);
        sb.append(",");
        sb.append(leafId);

        Query qry  = em.createNativeQuery(sb.toString());

        qry.setParameter(1, rootId);

        JpaResultMapper jpaResultMapper = new JpaResultMapper();

        return jpaResultMapper.list(qry, TreePath.class);
    }
}

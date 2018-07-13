package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.TreePath;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TreePathRepositoryImpl extends QueryDslRepositorySupport implements TreePathRepositoryCustom {

    public TreePathRepositoryImpl () {
        super(TreePath.class);
    }

    public void createTreePathByRootIdAndLeafId (int rootId, int leafId) {
        EntityManager em = getEntityManager();

        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO tree_paths (ancestor, descendant) SELECT t.ancestor, ");
        sb.append(leafId);
        sb.append("  FROM tree_paths t WHERE t.descendant = ?1 UNION ALL SELECT ");
        sb.append(leafId);
        sb.append(",");
        sb.append(leafId);

        Query qry  = em.createNativeQuery(sb.toString());

        qry.setParameter(1, rootId);

        qry.executeUpdate();

        em.close();
    }

    public void detachFromTree (int subTreeRootId) {
        StringBuffer sb = new StringBuffer();

        sb.append("DELETE\n");
        sb.append("  FROM tree_paths\n");
        sb.append(" WHERE descendant IN (\n");
        sb.append("                      SELECT descendant\n");
        sb.append("                        FROM tree_paths\n");
        sb.append("                       WHERE ancestor = ?1\n");
        sb.append("                     )\n");
        sb.append("   AND ancestor IN (\n");
        sb.append("                     SELECT ancestor\n");
        sb.append("                       FROM tree_paths\n");
        sb.append("                      WHERE descendant = ?2\n");
        sb.append("                        AND ancestor != descendant\n");
        sb.append("                   )");

        EntityManager em = getEntityManager();

        Query qry = em.createNativeQuery(sb.toString());

        qry.setParameter(1, subTreeRootId);
        qry.setParameter(2, subTreeRootId);

        qry.executeUpdate();

        em.close();;
    }

    public void moveSubTreeTo (int subTreeRootId, int moveTo) {
        StringBuffer sb = new StringBuffer();

        sb.append("INSERT INTO tree_paths (ancestor, descendant)\n");
        sb.append("SELECT tree.ancestor, subtree.descendant\n");
        sb.append("  FROM tree_paths AS tree\n");
        sb.append("CROSS JOIN tree_paths AS subtree\n");
        sb.append(" WHERE tree.descendant = ?1\n");
        sb.append("   AND subtree.ancestor = ?2\n");

        EntityManager em = getEntityManager();

        Query qry = em.createNativeQuery(sb.toString());

        qry.setParameter(1, moveTo);
        qry.setParameter(2, subTreeRootId);

        qry.executeUpdate();

        em.close();;
    }

}

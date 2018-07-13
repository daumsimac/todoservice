package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.TreePath;

import java.util.List;

public interface TreePathRepositoryCustom {
    void createTreePathByRootIdAndLeafId (int rootId, int leafId);

    List<TreePath> getTreePathByRootIdAndLeafId (int rootId, int leafId);

    void deleteAncestorsWithoutSelf (int id);

    void moveSubTreeTo (int subTreeRootId, int moveTo);

    List<Integer> getAncestorByDescendantWithoutSelf (int id);
}

package com.kakaopay.todolist.repository;

import com.kakaopay.todolist.entity.TreePath;

import java.util.List;

public interface TreePathRepositoryCustom {
    void createTreePathByRootIdAndLeafId (int rootId, int leafId);

    void detachFromTree (int subTreeRootId);

    void moveSubTreeTo (int subTreeRootId, int moveTo);
}

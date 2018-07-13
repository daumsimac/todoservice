package com.kakaopay.todolist.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name="todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "content")
    private String content;

    @Column(name = "display_content")
    private String displayContent;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "completed_at")
    private Date completedAt;

    @OneToMany(mappedBy = "descendant", targetEntity = TreePath.class, fetch = FetchType.LAZY)
    private List<TreePath> ancestors;

    @OneToMany(mappedBy = "ancestor", targetEntity = TreePath.class, fetch = FetchType.LAZY)
    private List<TreePath> descendants;
}

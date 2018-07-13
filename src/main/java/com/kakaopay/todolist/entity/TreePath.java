package com.kakaopay.todolist.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(
        name="tree_paths",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ancestor", "descendant"})
        }
)
public class TreePath {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ancestor")
    private Integer ancestor;

    @Column(name = "descendant")
    private Integer descendant;
}

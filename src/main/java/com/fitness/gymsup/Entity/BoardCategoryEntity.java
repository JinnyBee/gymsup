package com.fitness.gymsup.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "board_category")
public class BoardCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;                 //게시판 카테고리 id

    @Column(name = "category", length = 50, nullable = false, unique = true)
    private String category;            //게시판 카테고리명
}

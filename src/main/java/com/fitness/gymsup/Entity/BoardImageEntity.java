package com.fitness.gymsup.Entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SequenceGenerator(
        name = "board_SEQ",
        sequenceName = "board_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "board")
public class BoardEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "board_SEQ")
    private Integer id;         //게시판 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BoardCategoryEntity category; //board_category 테이블의 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;    //user 테이블의 id

    @Column(name = "title", length = 100, nullable = false)
    private String  title;      //제목

    @Column(name = "content", length = 500, nullable = false)
    private String  content;    //내용

    @Column(name = "good_cnt")
    @ColumnDefault("0")
    private Integer goodCnt;    //추천수

    @Column(name = "view_cnt")
    @ColumnDefault("0")
    private Integer viewCnt;    //조회수
}

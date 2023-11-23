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
        name = "comment_SEQ",
        sequenceName = "comment_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "comment")
public class CommentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "comment_SEQ")
    private Integer id;                 //댓글 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;    //참조 테이블 Entity명 (board 테이블)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;      //참조 테이블 Entity명 (user 테이블 - 댓글 작성자)

    @Column(name = "comment", length = 200, nullable = false)
    private String  comment;            //댓글 내용

    @Column(name = "good_cnt")
    @ColumnDefault("0")
    private Integer goodCnt;            //추천수
}
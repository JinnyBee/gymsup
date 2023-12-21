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
        name = "reply_SEQ",
        sequenceName = "reply_SEQ",
        initialValue = 64,
        allocationSize = 1
)
@Table(name = "reply")
public class ReplyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "reply_SEQ")
    private Integer id;                     //답글 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentEntity;    //참조 테이블 Entity명 (comment 테이블)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;          //참조 테이블 Entity명 (user 테이블 - 답글 작성자)

    @Column(name = "reply", length = 200, nullable = false)
    private String  reply;                  //답글 내용
}
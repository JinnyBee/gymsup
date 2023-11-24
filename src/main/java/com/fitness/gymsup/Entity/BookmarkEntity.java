package com.fitness.gymsup.Entity;

import com.fitness.gymsup.Constant.BookmarkType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SequenceGenerator(
        name = "bookmark_SEQ",
        sequenceName = "bookmark_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "bookmark")
public class BookmarkEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "bookmark_SEQ")
    private Integer id;                 //북마크 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;    //참조 테이블 Entity명 (board 테이블 - 북마크 게시글)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;      //참조 테이블 Entity명 (user 테이블 - 북마크 선택한 사용자)

    @Column(name = "bookmark_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookmarkType bookmarkType;  //북마크 타입 (열거형)
}

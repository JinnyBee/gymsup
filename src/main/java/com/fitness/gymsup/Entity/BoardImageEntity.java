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
        name = "board_image_SEQ",
        sequenceName = "board_image_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "board_image")
public class BoardImageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "board_image_SEQ")
    private Integer id;         //게시판 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;  //참조 테이블명 (board 테이블)

    @Column(name = "img_file", length = 255, nullable = false)
    private String  imgFile;    //이미지 파일
}

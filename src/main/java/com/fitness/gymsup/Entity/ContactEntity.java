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
@SequenceGenerator(
        name = "contact_SEQ",
        sequenceName = "contact_SEQ",
        initialValue = 33,
        allocationSize = 1
)
@Table(name = "contact")
public class ContactEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "contact_SEQ")
    private Integer id;             //문의 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;  //유저 정보

    @Column(name = "title", length = 100, nullable = false)
    private String  title;          //문의 제목

    @Column(name = "content", length = 500, nullable = false)
    private String  content;        //문의 내용

    @Column(name= "answer", length = 500)
    private String answer;          //답변

    @Column(name= "is_answer",nullable = false)
    private boolean is_answer;      //답변 여부
}

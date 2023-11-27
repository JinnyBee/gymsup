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
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "contact")
public class ContactEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "contact_SEQ")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "title", length = 100, nullable = false)
    private String  title;

    @Column(name = "content", length = 500, nullable = false)
    private String  content;

    @Column(name= "answer", length = 500, nullable = false)
    private String answer;

    @Column(name= "is_answer",nullable = false)
    private boolean is_answer;
}

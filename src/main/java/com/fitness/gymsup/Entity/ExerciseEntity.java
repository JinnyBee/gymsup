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
        name = "exercise_SEQ",
        sequenceName = "exercise_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "exercise")
public class ExerciseEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "exercise_SEQ")
    private Integer id;

    @Column(name = "exercisename", nullable = false)
    private String exercisename;

    @Column(name = "minute", nullable = false)
    private Integer minute;

    @Column(name = "kcal", nullable = false)
    private Integer kcal;
}

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
        name = "machine_info_SEQ",
        sequenceName = "machine_info_SEQ",
        initialValue = 1,
        allocationSize = 1
)

@Table(name = "machine_info")
public class MachineInfoEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "machine_info_SEQ")
    private int id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "content", length = 500, nullable = false)
    private String content;

    @Column(name ="img_file", length = 255,nullable = false)
    private String img;

    @Column(name ="result", length = 50, nullable = false)
    private String result;
}

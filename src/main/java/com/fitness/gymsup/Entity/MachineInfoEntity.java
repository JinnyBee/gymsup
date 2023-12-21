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
        initialValue = 6,
        allocationSize = 1
)

@Table(name = "machine_info")
public class MachineInfoEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "machine_info_SEQ")
    private int id;         //운동기구 번호

    @Column(name = "name", length = 50, nullable = false)
    private String name;    //운동기구 이름

    @Column(name = "content", length = 500, nullable = false)
    private String content; //운동기구 설명

    @Column(name ="img_file", length = 255,nullable = false)
    private String img;     //운동기구 이미지

    @Column(name ="result", length = 50, nullable = false)
    private String result;  //운동기구 감지한 결과 (플라스크에서 인식한 class name)
}

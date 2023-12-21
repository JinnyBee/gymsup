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
        name = "machine_usage_SEQ",
        sequenceName = "machine_usage_SEQ",
        initialValue = 33,
        allocationSize = 1
)

@Table(name = "machine_usage")
public class MachineUsageEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "machine_usage_SEQ")
    private int id;                                 //운동 기구 영상 번호

    @Column(name = "title", length = 100, nullable = false)
    private String title;                           //운동 기구 영상 제목

    @Column(name = "content", length = 500, nullable = false)
    private String content;                         //운동 기구 영상 설명

    @Column(name = "url", length = 200, nullable = false)
    private String url;                             //운동 기구 영상 주소

    @Column(name = "thumbnail", length = 255)
    private String thumbnail;                       //운동 기구 영상 썸네일

    @Column(name = "view_cnt")
    @ColumnDefault("0")
    private int viewCnt;                            //운동 기구 영상 조회수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_info_id")
    private MachineInfoEntity machineInfoEntity;    //운동 기구 정보
}

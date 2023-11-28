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
        initialValue = 1,
        allocationSize = 1
)

@Table(name = "machine_usage")
public class MachineUsageEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "machine_usage_SEQ")
    private int id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", length = 500, nullable = false)
    private String content;

    @Column(name = "url", length = 200, nullable = false)
    private String url;

    @Column(name = "thumbnail", length = 255)
    private String thumbnail;

    @Column(name = "view_cnt")
    @ColumnDefault("0")
    private int viewCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_info_id")
    private MachineInfoEntity machineInfoEntity;
}

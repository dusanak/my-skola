package com.skillsfighters.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "ui_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UIOptionsDTO {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "group_id")
    private long groupId;
    @Column(name = "color")
    private Integer color;
    @Column(name = "unit")
    private String unit;
    @Column(name = "icon")
    private String icon;

    public UIOptions toUIOptions() {
        return UIOptions.builder()
                .id(id)
                .groupId(groupId)
                .color(getColor())
                .unit(getUnit())
                .icon(getIcon())
                .build();
    }
}

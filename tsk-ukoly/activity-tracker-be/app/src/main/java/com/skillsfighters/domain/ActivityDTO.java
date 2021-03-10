package com.skillsfighters.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
public class ActivityDTO {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "happening")
    private Date timestamp;
    @Column(name = "group_id")
    private long groupId;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    public Activity toActivity() {
        return Activity.builder()
                .id(id)
                .timestamp(timestamp.getTime())
                .groupId(groupId)
                .createdAt(createdAt.getTime())
                .updatedAt(updatedAt.getTime())
                .build();
    }
}

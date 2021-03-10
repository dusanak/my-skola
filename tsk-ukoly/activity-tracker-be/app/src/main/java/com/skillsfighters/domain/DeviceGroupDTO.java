package com.skillsfighters.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "device_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceGroupDTO {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "firebase_uid")
    private String firebaseUid;
    @Column(name = "fcm_tokens")
    @ElementCollection
    private List<String> FCMTokens;

    public DeviceGroup toDeviceGroup() {
        return DeviceGroup.builder()
                .id(id)
                .firebaseUid(firebaseUid)
                .FCMTokens(FCMTokens)
                .build();
    }
}

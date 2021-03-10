package com.skillsfighters.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(builder = User.UserBuilder.class)
public class UserDTO {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "firebase_uid")
    private String firebaseUid;

    @JsonDeserialize(builder = User.UserBuilder.class)

    public User toUser() {
        return User.builder()
                .id(id)
                .firebaseUid(firebaseUid)
                .build();
    }
}

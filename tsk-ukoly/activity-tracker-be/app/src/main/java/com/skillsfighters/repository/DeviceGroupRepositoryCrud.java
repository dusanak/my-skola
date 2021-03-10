package com.skillsfighters.repository;

import com.skillsfighters.domain.DeviceGroupDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DeviceGroupRepositoryCrud extends CrudRepository<DeviceGroupDTO, Long> {

    @Query(value = "SELECT id, firebase_uid FROM device_groups WHERE firebase_uid = ?1",
            nativeQuery = true)
    Optional<DeviceGroupDTO> findByUserId(String FirebaseUID);
}

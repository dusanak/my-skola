package com.skillsfighters.repository;

import com.skillsfighters.domain.UserDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepositoryCrud extends CrudRepository<UserDTO, Long> {
    @Query(value = "SELECT firebase_uid, id FROM users WHERE firebase_uid = ?1",
            nativeQuery = true)
    Optional<UserDTO> findByUid(String uid);
}

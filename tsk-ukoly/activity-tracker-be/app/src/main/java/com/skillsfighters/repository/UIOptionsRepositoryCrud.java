package com.skillsfighters.repository;

import com.skillsfighters.domain.UIOptionsDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UIOptionsRepositoryCrud extends CrudRepository<UIOptionsDTO, Long> {

    @Query(value = "SELECT * FROM ui_options WHERE group_id = ?1",
            nativeQuery = true)
    Optional<UIOptionsDTO> findByGroupId(Long groupId);
}

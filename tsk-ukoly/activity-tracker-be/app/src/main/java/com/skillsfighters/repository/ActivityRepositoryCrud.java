package com.skillsfighters.repository;

import com.skillsfighters.domain.ActivityDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityRepositoryCrud extends CrudRepository<ActivityDTO, Long> {
    
    @Query(value = "SELECT id, happening, group_id, created_at, updated_at, is_deleted FROM activities WHERE group_id = ?1 and is_deleted = false",
            nativeQuery = true)
    List<ActivityDTO> findAllByGroupId(Long groupId);
}

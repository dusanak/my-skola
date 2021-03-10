package com.skillsfighters.security;

import com.skillsfighters.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public interface SecurityInfo {
    default Optional<String> getLoggedUserFirebaseUid() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            final User user = (User) authentication.getDetails();
            return Optional.of(user.getFirebaseUid());
        } else {
            return Optional.empty();
        }
    }

    default Optional<Long> getLoggedUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            final User user = (User) authentication.getDetails();
            return Optional.of(user.getId());
        } else {
            return Optional.empty();
        }
    }

    default String printLoggedUserFirebaseUid() {
        return getLoggedUserFirebaseUid().orElse("N/A");
    }

    default  String printLoggedUserId() {
        return getLoggedUserId().map(id -> id.toString()).orElse("N/A");
    }
}

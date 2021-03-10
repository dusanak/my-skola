package com.skillsfighters.security;

import com.skillsfighters.domain.UserDTO;
import com.skillsfighters.repository.UserRepositoryCrud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.skillsfighters.security.FirebaseUtils.checkAndInitializeFirebase;

@Slf4j
@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {
    @Value("${firebase.debug.token:#{null}}")
    private Optional<String> firebaseDebugToken;
    private final UserRepositoryCrud userRepository;

    @Autowired
    public FirebaseAuthenticationProvider(UserRepositoryCrud userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        checkAndInitializeFirebase();
        final FirebaseAuthenticationToken firebaseAuthentication = (FirebaseAuthenticationToken) authentication;
        final String firebaseUid = firebaseAuthentication.getUid();

        final Optional<UserDTO> user = userRepository.findByUid(firebaseUid);
        if (user.isPresent()) {
            log.debug("FirebaseAuthenticationProvider: user: {} successfully logged in", firebaseUid);
            return new UserDetails(user.get().toUser());
        } else if (firebaseDebugToken.isPresent() && firebaseDebugToken.get().equals(firebaseUid)) {
            final UserDTO userToSave = UserDTO.builder().firebaseUid(firebaseDebugToken.get()).build();
            final UserDTO debugUser = userRepository.save(userToSave);
            return new UserDetails(debugUser.toUser());
        } else {
            final UserDTO savedUser = userRepository.save(UserDTO.builder().firebaseUid(firebaseUid).build());
            log.debug("FirebaseAuthenticationProvider: user: {} successfully inserted into DB", firebaseUid);
            return new UserDetails(savedUser.toUser());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

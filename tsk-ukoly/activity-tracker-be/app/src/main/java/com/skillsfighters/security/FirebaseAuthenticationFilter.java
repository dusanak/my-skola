package com.skillsfighters.security;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.skillsfighters.security.FirebaseUtils.checkAndInitializeFirebase;

//This class serves as main firebase filter
@Slf4j
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private final Optional<String> firebaseDebugToken;

    FirebaseAuthenticationFilter() {
        firebaseDebugToken = Optional.empty();
    }

    FirebaseAuthenticationFilter(final Optional<String> firebaseDebugToken) {
        this.firebaseDebugToken = firebaseDebugToken;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        firebaseTokenCheck(request, response, filterChain);
    }

    /*
    This method takes a firebase token and checks if it is equals to a debug token that is set up in application.properties
    or verify if it is the valid token
     */
    private void firebaseTokenCheck(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        checkAndInitializeFirebase();
        final String firebaseToken = request.getHeader("firebaseToken");
        if (firebaseDebugToken.isPresent() && firebaseDebugToken.get().equals(firebaseToken)) {
            final Authentication authentication = new FirebaseAuthenticationToken(firebaseToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } else {
            try {
                final FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
                final String uid = decodedToken.getUid();
                // Create custom Authentication and let Spring know about it
                final Authentication authentication = new FirebaseAuthenticationToken(uid);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
            } catch (FirebaseAuthException firebaseAuthException) {
                log.warn("Firebase token not verified value is: {}", firebaseToken);
            }
        }
    }
}
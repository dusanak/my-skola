package com.skillsfighters.security;

import java.util.Arrays;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final String uid;

    public FirebaseAuthenticationToken(final String uid) {
        super(Arrays.asList());
        this.uid = uid;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public String getUid() {
        return uid;
    }
}
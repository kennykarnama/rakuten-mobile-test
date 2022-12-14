package com.rakutenmobile.messageapi.usermessage.adapter.in.restful.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class TokenHolder extends AbstractAuthenticationToken {

    private String token;
    private UserDetails userDetails;

    public TokenHolder(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
    }

    public TokenHolder(UserDetails userDetails,
                                  String token,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.userDetails = userDetails;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.userDetails;
    }
}

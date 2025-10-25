package com.overtheinfinite.splittodo.auth.dto;

import com.overtheinfinite.splittodo.auth.domain.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final String nickname;
    private final String socialId;
    private final User.AuthProvider authProvider;

    @Builder
    public CustomUserDetails(String username, String password, String nickname, String socialId, User.AuthProvider authProvider) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.socialId = socialId;
        this.authProvider = authProvider;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

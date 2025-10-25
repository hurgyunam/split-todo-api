package com.overtheinfinite.splittodo.auth.service;

import com.overtheinfinite.splittodo.auth.UserRepository;
import com.overtheinfinite.splittodo.auth.domain.User;
import com.overtheinfinite.splittodo.auth.dto.SignupRequest;
import com.overtheinfinite.splittodo.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public User registerLocalUser(SignupRequest request) {

        // 1. 중복 확인 (ID와 닉네임)
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 2. 평문 비밀번호를 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. User 엔티티 생성 및 저장
        User newUser = User.localBuilder()
                .username(request.getUsername())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .nickname(request.getNickname())
                .build();

        return userRepository.save(newUser);
    }

    public boolean authenticate(String username, String rawPassword) {

        // 1. Username으로 User 정보 조회
        User user = userRepository.findByUsername(username)
                .orElse(null); // 사용자가 없으면 null 반환

        if (user == null || user.getAuthProvider() != User.AuthProvider.LOCAL) {
            // 사용자가 없거나 소셜 로그인 사용자라면 실패
            return false;
        }

        // 2. 평문 비밀번호와 DB의 암호화된 비밀번호를 비교 (핵심!)
        // .matches()가 BCrypt 암호화 로직을 사용하여 안전하게 비교합니다.
        String encodedPassword = user.getPassword();

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. username으로 DB에서 사용자 정보(Entity)를 찾습니다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. 찾은 Entity 정보를 기반으로 UserDetails 객체를 만들어서 반환합니다.
        // Spring Security의 User 객체는 비밀번호, 사용자 이름, 권한을 포함합니다.
        return CustomUserDetails.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .password(user.getPassword())
                .build();
    }
}

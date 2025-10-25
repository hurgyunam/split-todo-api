package com.overtheinfinite.splittodo.auth;
import com.overtheinfinite.splittodo.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * username (일반 로그인 ID)을 기준으로 User 엔티티를 조회합니다.
     * Spring Security의 인증 로직에서 주로 사용됩니다.
     */
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);

    Optional<User> findBySocialId(String socialId);

    // socialId와 authProvider로 소셜 로그인 사용자 조회 메서드를 추가할 수도 있습니다.
    Optional<User> findBySocialIdAndAuthProvider(String socialId, User.AuthProvider authProvider);
}
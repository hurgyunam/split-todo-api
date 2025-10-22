package com.overtheinfinite.splittodo.util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    /**
     * 현재 SecurityContext에 저장된 Authentication 객체에서 사용자 정보를 추출합니다.
     * @return Authentication 객체 또는 인증 정보가 없으면 null
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 현재 인증된 사용자의 ID(Principal)를 Long 타입으로 가져옵니다.
     * 이 메서드는 주로 Service 또는 Controller에서 사용됩니다.
     * @return 인증된 사용자의 ID (Long)
     * @throws RuntimeException 인증 정보가 없는 경우 예외 발생
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getCurrentAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증 정보가 없거나 익명 사용자(anonymousUser)인 경우
            throw new RuntimeException("SecurityContext에 인증 정보가 없습니다.");
        }

        // Principal 객체는 보통 Long 타입의 ID 또는 UserDetails 객체입니다.
        // 여기서는 Long ID를 저장했다고 가정하고 코드를 작성합니다.
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        }

        // UserDetails 객체인 경우 (가장 일반적인 경우)
        // UserDetails 구현체에서 ID를 추출하는 로직이 필요합니다.
        // 예: if (principal instanceof UserDetailsImpl) { return ((UserDetailsImpl) principal).getUserId(); }

        // String 타입일 경우 Long으로 변환 (예: "1")
        if (principal instanceof String) {
            try {
                return Long.valueOf((String) principal);
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 경우 처리
                throw new RuntimeException("Principal이 유효한 Long ID 형식이 아닙니다: " + principal, e);
            }
        }

        throw new RuntimeException("Principal 객체가 예상치 않은 타입입니다.");
    }
}
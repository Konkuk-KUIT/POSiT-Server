package com.posit.posit.domain.user.dto;

import com.posit.posit.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter // [중요] 이게 있어야 컨트롤러에서 .getId()를 할 수 있습니다!
public class UserPrincipal implements UserDetails {

    private final Long id;          // DB PK (우리가 필요한 것)
    private final String username;  // 로그인 ID (email or loginId)
    private final String password;  // 암호화된 비밀번호
    private final Collection<? extends GrantedAuthority> authorities; // 권한 (ROLE_USER 등)

    // 생성자
    public UserPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // [핵심] User 엔티티 -> UserPrincipal 변환 메서드
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),       // 엔티티의 ID를 여기에 복사!
                user.getLoginId(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    // --- 아래는 UserDetails 인터페이스 필수 구현 메서드들 (그냥 붙여넣으세요) ---

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
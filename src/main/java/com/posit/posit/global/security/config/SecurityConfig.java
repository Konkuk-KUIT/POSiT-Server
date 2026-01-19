package com.posit.posit.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posit.posit.global.jwt.JwtUtil;
import com.posit.posit.global.security.filter.JwtAuthenticationFilter;
import com.posit.posit.global.security.handler.RestAccessDeniedHandler;
import com.posit.posit.global.security.handler.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) //401
                        .accessDeniedHandler(accessDeniedHandler) //403
                )
                .authorizeHttpRequests(auth -> auth
                        // 공개 API (토큰 없이 접근 가능)
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/api/ping"
                        ).permitAll()
                        // 사장님 전용(대시보드/관리)
                        .requestMatchers("/owner/**").hasRole("OWNER")
                        // 사장님만 가능한 액션
                        .requestMatchers("/stores/*/concerns").hasRole("OWNER")
                        .requestMatchers("/concerns/*").hasRole("OWNER")
                        .requestMatchers("/memos/*/adopt", "/memos/*/reject").hasRole("OWNER")
                        .anyRequest().authenticated()
                )

                // 토큰이 있으면 인증 세팅, 토큰이 없으면 그냥 통과(권한 체크는 Security가 담당)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, objectMapper),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
//todo:@PreAuthorize("hasRole('OWNER')") 로 컨트롤러 단에서 권한 막기 (URL만으로 구분 어려운 memos/memoId는 둘다지만 수정은 게스트만)

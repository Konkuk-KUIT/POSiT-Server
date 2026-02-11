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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

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
                // 👇 1. CORS 설정을 가장 먼저 추가해야 합니다!
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

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
                        // Preflight Request(OPTIONS)는 무조건 허용해줘야 함
                        .requestMatchers(org.springframework.web.cors.CorsUtils::isPreFlightRequest).permitAll()

                        // 공개 API
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/api/ping",
                                "/stores" // 가게 등록은 열려있어야 함 (로그인 필요하면 제외)
                        ).permitAll()
                        // 사장님 전용
                        .requestMatchers("/owner/**").hasRole("OWNER")
                        // ... 나머지 설정
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, objectMapper),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // 👇 2. 허용할 도메인 설정 (여기 Vercel 주소 넣으세요!)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin 목록
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",// 로컬 프론트엔드
                "https://kuit-6th-posit.vercel.app"  //  본인의 Vercel 배포 주소 (뒤에 슬래시 / 뺄것)

        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 헤더
        configuration.setAllowedHeaders(Collections.singletonList("*"));

        // 쿠키나 인증 정보 허용 (필수)
        configuration.setAllowCredentials(true);

        // 노출할 헤더 (프론트에서 Authorization 헤더 등을 읽어야 한다면 추가)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
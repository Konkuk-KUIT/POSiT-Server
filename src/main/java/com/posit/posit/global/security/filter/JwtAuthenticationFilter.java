package com.posit.posit.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.error.ErrorCode;
import com.posit.posit.global.error.ErrorResponse;
import com.posit.posit.global.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.MediaType;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
//        System.out.println("path = " + path);
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        return path.startsWith("/swagger-ui")
                || path.startsWith("/auth")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator/health")
                || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            // 토큰이 없으면 그냥 통과 (권한 판단은 SecurityConfig가 담당)
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 타입 검증 (ACCESS만 허용)
            jwtUtil.assertAccessToken(token);

            Long userId = jwtUtil.getUserId(token);

            // role -> authorities 세팅
            String role = jwtUtil.getRole(token).orElse("GUEST");
            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UserPrincipal principal = new UserPrincipal(
                    userId,
                    String.valueOf(userId),
                    "N/A",
                    authorities
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            writeErrorResponse(response, ErrorCode.INVALID_TOKEN);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        String token = auth.substring(7);
        if (token == null || token.isBlank()) {
            return null;
        }
        return token;
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse body = ErrorResponse.fail(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}
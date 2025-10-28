package springboot_first.pr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod; // HttpMethod 추가 임포트

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. 로그인, 회원가입 관련 경로는 모두 허용
                .requestMatchers(
                    "/api/auth/**"  // 예: /api/auth/login, /api/auth/register 등
                ).permitAll()
                
                // 2. 게시글 목록 조회 (GET)은 로그인 없이 모두 허용
                .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/*").permitAll()
                
                // 3. 게시글 작성(POST), 수정(PUT), 삭제(DELETE)는 로그인한 사용자만 허용
                .requestMatchers(HttpMethod.POST, "/api/posts").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/posts/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*").authenticated()
                
                // 4. 나머지 모든 요청은 로그인한 사용자에게만 허용 (혹시 모를 다른 API 포함)
                .anyRequest().authenticated()
            )
            .csrf().disable() // CSRF 비활성화 (API 통신에서는 일반적으로 비활성화)
            .formLogin().disable(); // 기본 로그인 폼 비활성화
            
        // 세션 기반 인증이 아닌 JWT 등을 사용할 경우, 추가 설정 필요
        
        return http.build();
    }
}
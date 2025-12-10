package springboot_first.pr.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import springboot_first.pr.security.JwtAuthenticationEntryPoint;
import springboot_first.pr.security.JwtAuthenticationFilter;

import java.util.List;

/**
 * JWT 기반 인증을 위한 Spring Security 설정 클래스
 */
@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 필터와 예외 처리 핸들러를 주입받습니다.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; 
    
    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈 등록 (BCrypt 사용)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security의 주요 필터 체인을 정의합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS 설정: corsConfigurationSource 빈을 사용하여 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. CSRF 비활성화: REST API 및 토큰 기반 인증 시 필수
            .csrf(AbstractHttpConfigurer::disable)
            
            // 3. 세션 관리: STATELESS(무상태)로 설정 (JWT 인증 시 세션 사용 안 함)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4. 인증/인가 예외 처리 등록
            // 인증되지 않은 사용자가 보호된 리소스에 접근 시 401 응답 처리
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // 5. 인증/권한 설정
            .authorizeHttpRequests(auth -> auth
                // '/api/auth/' 경로는 로그인, 회원가입 등 인증 없이 모두 접근 허용
                .requestMatchers("/api/auth/**").permitAll() 

                // ✅ 게시글 조회 (GET)은 인증 없이 모두 접근 허용 (비회원도 게시글을 볼 수 있도록)
                .requestMatchers(HttpMethod.POST, "/api/posts/**").permitAll()
                
                // 나머지 모든 요청은 인증(로그인/토큰 유효)을 요구합니다.
                .anyRequest().authenticated() 
            )
        ;
        
        // 6. 커스텀 JWT 필터를 UsernamePasswordAuthenticationFilter 이전에 추가하여 토큰 인증 수행
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS (Cross-Origin Resource Sharing) 설정 빈 등록
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 출처(Origin) 목록 설정
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"));
        
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); 
        
        // 자격 증명(쿠키, 인증 헤더 등) 허용
        configuration.setAllowCredentials(true);
        
        // 허용할 헤더 설정 (모든 헤더 허용)
        configuration.setAllowedHeaders(List.of("*"));
        
        // Preflight 요청 캐시 시간
        configuration.setMaxAge(3600L); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 위 설정 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
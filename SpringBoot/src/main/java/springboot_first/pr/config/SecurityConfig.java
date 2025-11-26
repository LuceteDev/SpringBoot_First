// package springboot_first.pr.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.cors.CorsConfigurationSource;

// import java.util.List;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     // Spring Security HTTP 설정
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .cors()  // CORS 허용
//             .and()
//             .csrf().disable() // 개발용: CSRF 비활성화
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/api/**").permitAll() // API 경로 허용
//                 .anyRequest().authenticated()
//             );
//         return http.build();
//     }

//     // CORS 설정
//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         configuration.setAllowedOrigins(List.of("http://localhost:5173")); // React 주소
//         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         configuration.setAllowCredentials(true);
//         configuration.setAllowedHeaders(List.of("*"));

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);

//         return source;
//     }
// }

package springboot_first.pr.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 1. PasswordEncoder 빈 등록 (BCrypt 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Spring Security HTTP 설정 (인증/권한 및 CORS 설정 포함)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 💡 CORS 설정: corsConfigurationSource 빈을 자동으로 찾아 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 💡 CSRF 비활성화: REST API 및 토큰 기반 인증 시 필수
            .csrf(AbstractHttpConfigurer::disable)
            
            // 💡 세션 관리: STATELESS로 설정 (JWT 등 토큰 기반 인증 시 필수)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 💡 인증/권한 설정
            .authorizeHttpRequests(auth -> auth
                // 🚨 로그인, 회원가입 경로는 인증 없이 모두 접근 허용 (403 에러 해결)
                // .requestMatchers("/api/auth/**").permitAll() 
                .anyRequest().permitAll() // 모든 요청 허용 (인증 무시)
                
                // 💡 그 외 모든 요청은 인증(로그인)을 요구합니다.
                // .anyRequest().authenticated()
            );

        return http.build();
    }

    // 3. CORS 설정 빈 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 💡 프론트엔드 주소 (React) 허용
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        
        // 💡 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 💡 쿠키, 인증 헤더 등을 허용
        configuration.setAllowCredentials(true);
        
        // 💡 모든 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));
        
        // 💡 캐싱된 CORS 설정의 유효 시간 (초)
        configuration.setMaxAge(3600L); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 CORS 설정 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
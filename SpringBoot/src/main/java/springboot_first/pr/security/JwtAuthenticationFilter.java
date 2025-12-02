package springboot_first.pr.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 💡 JWT 토큰을 요청 헤더에서 추출하고 검증하여 SecurityContext에 인증 정보를 설정하는 필터
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    // 💡 JWT를 통해 사용자 ID만 알 수 있고 DB를 조회하지 않으므로, 더미 유저를 만들 필요가 없습니다.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 1. 요청 헤더에서 JWT 추출 (Bearer 스킴 파싱)
            String jwt = getJwtFromRequest(request);

            if (jwt != null && !jwt.trim().isEmpty()) {
                // 2. JWT 유효성 검증 및 userId 추출
                String userId = tokenProvider.getUserIdFromToken(jwt); 

                // 3. 추출된 userId로 인증 객체 생성
                // 이 userId 문자열이 @AuthenticationPrincipal에 주입됩니다.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, // Principal: 사용자 ID (String 타입)
                        null,  // Credential: 비밀번호는 이미 검증되었으므로 null
                        null  // Authorities: 권한은 현재 사용하지 않으므로 null
                );
                
                // 4. 인증 객체에 웹 상세 정보 추가 (선택 사항)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. SecurityContext에 Authentication 객체 설정 (인증 완료)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Security Context에 사용자 인증 정보를 설정할 수 없습니다.", ex);
            // 인증 실패 시 JWT 예외는 GlobalExceptionHandler에서 처리되도록 필터에서 예외를 던지지 않습니다.
        }

        // 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT를 추출합니다. (Bearer <token> 형식)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        // "Bearer "로 시작하는지 확인하고 토큰 부분만 반환
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
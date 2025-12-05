package springboot_first.pr.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring Security에서 인증되지 않은 사용자(토큰이 없거나 무효한 사용자)가 
 * 보호된 리소스에 접근하려 할 때 호출되는 핸들러입니다.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("인증되지 않은 접근: URI: {}, 에러 메시지: {}", request.getRequestURI(), authException.getMessage());
        
        // 401 Unauthorized 상태 코드를 설정합니다.
        // 클라이언트에게 토큰이 없거나 유효하지 않음을 명확히 알립니다.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication required or token is invalid.");
    }
}
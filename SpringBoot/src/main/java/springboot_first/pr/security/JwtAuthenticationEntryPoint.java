package springboot_first.pr.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security에서 인증되지 않은 사용자(토큰이 없거나 무효한 사용자)가 보호된 리소스에 접근하려 할 때 호출되는 핸들러입니다.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // ✅ 1. ObjectMapper 인스턴스 필드로 선언
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("인증되지 않은 접근: URI: {}, 에러 메시지: {}", request.getRequestURI(), authException.getMessage());
        
        // 401 Unauthorized 상태 코드를 설정합니다.
        // 클라이언트에게 토큰이 없거나 유효하지 않음을 명확히 알립니다.
        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication required or token is invalid.");
        // 1. HTTP 상태 코드 설정 (401 Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
        
        // 2. 응답 컨텐츠 타입을 JSON으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        // 3. JSON 응답 본문 생성 (CommonResponse 구조를 따르도록 작성)
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "로그인이 필요하거나 인증 정보가 유효하지 않습니다."); // ⬅️ 클라이언트에게 보여줄 명확한 메시지
        body.put("error", "Unauthorized"); // 상세 오류 코드 또는 타입
        
        // 4. JSON 문자열로 변환하여 응답 출력
        String jsonResponse = objectMapper.writeValueAsString(body);
        
        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }
}
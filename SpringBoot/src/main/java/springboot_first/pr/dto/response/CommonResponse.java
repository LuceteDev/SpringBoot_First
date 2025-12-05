package springboot_first.pr.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// API 요청 성공/실패 시 공통으로 사용되는 표준 응답 구조입니다.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {

    // 1. 비즈니스 로직의 성공/실패 여부 (필수)
    private boolean success;

    // 2. 클라이언트에게 보여줄 메시지 (필수)
    private String message;
    
    // 3. 응답 발생 시점 (로그 및 추적에 유용)
    private LocalDateTime timestamp;

    // 4. 실제 응답 데이터 (로그인 정보, 조회 결과 등, 제네릭 타입 T)
    // 데이터가 없는 경우(로그아웃 등)는 null이 됩니다.
    private T data;

    // ===============================================
    // 💡 정적 팩토리 메서드: 성공 응답 (데이터 포함)
    // ===============================================

    public static <T> CommonResponse<T> success(String message, T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now()) // 현재 시간 자동 기록
                .build();
    }
    
    // ===============================================
    // 💡 정적 팩토리 메서드: 성공 응답 (데이터 없음, 로그아웃 등)
    // ===============================================

    public static CommonResponse<?> success(String message) {
        return CommonResponse.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now()) // 현재 시간 자동 기록
                // data 필드는 null로 남음
                .build();
    }

    // ===============================================
    // 💡 정적 팩토리 메서드: 실패 응답
    // ===============================================
    
    public static CommonResponse<?> failure(String message) {
        return CommonResponse.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now()) // 현재 시간 자동 기록
                // data 필드는 null로 남음
                .build();
    }
}
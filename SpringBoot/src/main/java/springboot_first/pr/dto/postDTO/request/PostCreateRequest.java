package springboot_first.pr.dto.postDTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 1️⃣ 어노테이션 선언
@NoArgsConstructor() // JSON 바인딩을 위한 Public 기본 생성자
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성

public class PostCreateRequest {
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    // 현업 팁: @Size(max = 5000) 등을 추가하여 DB TEXT 필드 크기에 맞춰 제한하기도 합니다.
    private String content;

    // ***************************************************************
    // ⚠️ 참고: 작성자 ID는 DTO에 포함하지 않습니다.
    // JWT 토큰에서 추출하여 서비스 계층에서 처리할 것입니다.
    // 이는 현업 REST API 설계의 모범 사례입니다.
    // ***************************************************************
}
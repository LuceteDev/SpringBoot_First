package springboot_first.pr.dto.postDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor() // JSON 바인딩을 위한 Public 기본 생성자
@ToString
public class PostUpdateRequest {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.") // 💡 100자 제한 추가
    private String title;
    
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    @Size(max = 5000, message = "내용은 5000자를 초과할 수 없습니다.") // 💡 5000자 제한 추가 (TEXT 컬럼 가정)
    private String content;
    
    // ⚠️ 이 DTO는 수정할 게시글 ID를 포함 ❌
    // ID는 컨트롤러에서 URL 경로(`@PathVariable`)를 통해 받게 됨!
}
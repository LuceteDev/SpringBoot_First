package springboot_first.pr.dto.postDTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// 1️⃣ 어노테이션 선언
@NoArgsConstructor() // JSON 바인딩을 위한 Public 기본 생성자
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@Setter // ⚠️ 검색 기능 구현하면서 처음 사용하는 setter : 컨트롤러가 파라미터를 담아줄 수 있게 Setter 허용

public class PostSearchRequest {
  private String title;
  private String content;
  private String username;

}
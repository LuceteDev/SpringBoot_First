package springboot_first.pr.dto.postDTO.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.entity.Post;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor(access = AccessLevel.PRIVATE) // private : @Builder 어노테이션이 정상적으로 작동하기 위한 보조 역할, 외부 생성 차단
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자의 접근 권한을 protected로 설정해서 외부 생성 차단, JPA는 허용하도록 설정
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성, ✅ 로깅과 디버깅을 위해 추가
@Builder // DTO 생성을 위한 빌더 패턴 추가 (테스트 코드 작성에 용이하다고 한다 ✅)
@Slf4j // 로깅 사용 -> 이 로깅 메시지에 객체의 상태를 담기 위해 @ToString을 함께 사용

public class PostListResponse {

  private Long postId;
  private String title;
  // 목록 조회에서는 content는 제외하고 필요한 정보만 제공
  
  private String authorUsername; // 목록에서 누가 썼는지 보여주기 위해 작성자 이름만 포함
  
  private LocalDateTime createdAt;
  
  // 정적 팩토리 메서드: Post 엔티티를 받아 DTO로 변환
  public static PostListResponse from(Post post) {
      return PostListResponse.builder()
              .postId(post.getId())
              .title(post.getTitle())
              .authorUsername(post.getUser().getUsername())
              .createdAt(post.getCreatedAt())
              .build();
    }

}

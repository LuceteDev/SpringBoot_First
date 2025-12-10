package springboot_first.pr.controller.post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.dto.postDTO.request.PostCreateRequest;
import springboot_first.pr.dto.postDTO.response.PostDetailResponse;
import springboot_first.pr.dto.response.CommonResponse;
import springboot_first.pr.exception.AuthenticationException;
import springboot_first.pr.repository.UserRepository;
import springboot_first.pr.service.post.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController // 1️⃣컨트롤러 선언 
@RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
@RequestMapping("/api/posts") // 3️⃣ 기본 경로 설정

public class PostController {
  
  private final PostService postService;
  

  // 〰️〰️〰️〰️〰️〰️〰️〰️ GET/POST/PATCH/DELETE 매핑 확인 〰️〰️〰️〰️〰️〰️〰️〰️ //

  
  // ⚠️ 현재 요청 DTO에는 title, content 2개의 필드만 있음


  /**
     * 게시글 생성 API (POST /api/posts)
     * - @Valid: 요청 DTO의 유효성 검증 (@NotBlank 등)을 수행합니다.
     * - @AuthenticationPrincipal: JWT를 통해 인증된 사용자 정보를 자동으로 주입받습니다.
   */
  @PostMapping // ⚠️ 글 작성은 매핑이 없음‼️
  public ResponseEntity<CommonResponse<PostDetailResponse>> createPost(
          @AuthenticationPrincipal String currentUserId,
          @Valid @RequestBody PostCreateRequest request) 
      {
          log.info("POST 게시글 생성 요청 접수. 요청 DTO: {}", currentUserId);
          
          // 1️⃣ 인증 정보 확인 및 사용자 ID 추출
          if (currentUserId == null || currentUserId == null) {
              log.error("인증 실패: UserDetails가 null이거나 사용자 ID가 없습니다.");
              throw new AuthenticationException("인증 정보가 없습니다. 로그인해주세요.");
          }
          
          // 2️⃣ Service 계층 호출 (서비스는 DTO만 반환)
          PostDetailResponse responseDto = postService.createPost(currentUserId, request);

          // 3️⃣ 💡 컨트롤러에서 응답 포장 (현업 표준)
          CommonResponse<PostDetailResponse> commonResponse = CommonResponse.success(
              "게시글이 성공적으로 작성되었습니다.", // 메시지
              responseDto                           // 데이터
          );
          
          log.info("게시글 생성 응답 성공: Status 201 Created. PostId: {}", responseDto.getPostId());
          
          return ResponseEntity
              .status(HttpStatus.CREATED) // HTTP 201 상태 코드 명시
              .body(commonResponse);       // CommonResponse를 본문으로 전달
    }
  
  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //
}

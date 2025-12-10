package springboot_first.pr.service.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import springboot_first.pr.dto.postDTO.request.PostCreateRequest;
import springboot_first.pr.dto.postDTO.request.PostUpdateRequest;
import springboot_first.pr.dto.postDTO.response.PostDetailResponse;
import springboot_first.pr.dto.postDTO.response.PostListResponse;
import springboot_first.pr.entity.Post;
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException;
// import springboot_first.pr.exception.ResourceNotFoundException;
import springboot_first.pr.repository.PostRepository;
import springboot_first.pr.repository.UserRepository;
import java.util.Objects; // 권한 확인을 위해 Objects.equals() 사용 예정

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입 (Dependency Injection)
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 기본 설정
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // 게시글 작성자 정보를 가져오기 위해 필요

    /**
     * 1️⃣ 게시글 생성 (CREATE)
     * - 인증된 사용자 ID를 받아 User 엔티티를 찾고 Post 엔티티에 연관관계를 설정해야 한다고 함!
     */
    @Transactional // 쓰기(Write) 작업이므로 별도의 트랜잭션 설정
    public PostDetailResponse createPost(String currentUserId, PostCreateRequest request) {
        
        // 1️⃣ 현재 로그인 사용자 (작성자) 엔티티 조회 (Security Context에서 가져온 ID 사용)
        User author = userRepository.findByUserId(currentUserId)
            .orElseThrow(() -> new AuthenticationException("작성자 정보를 찾을 수 없습니다."));

        // 2️⃣ Post 엔티티 생성 (빌더 패턴 사용)
        Post newPost = Post.create( // ⬅️ Post.builder() 대신 Post.create() 호출
                    request.getTitle(),
                    request.getContent(),
                    author // 조회된 User 엔티티 전달
                );
        
        // 3️⃣ DB에 저장
        Post savedPost = postRepository.save(newPost);

        // 4️⃣ 응답 DTO로 변환하여 반환
        return PostDetailResponse.from(savedPost);
    }


}
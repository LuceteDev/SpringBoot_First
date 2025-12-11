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


    /**
     * 2️⃣ 게시글 목록 조회 (READ - Pagination)
     * ⚠️ 현업에서 가장 중요하며, Pageable 객체를 그대로 Repository로 전달‼️
     * ⚠️ 여기에 트랜잭션을 명시하지 않고, 읽기 전용 트랜잭션 기본 설정을 클래스 레벨 설정으로 대체할 수 도 있다‼️
     */
    // @Transactional(readOnly = true) // ⚠️ 클래스 레벨 설정으로 대체 가능
    public Page<PostListResponse> findAllPosts(Pageable pageable) {

        // 1️⃣ Repository에서 페이징 처리된 Post 목록을 조회
        // ⚠️ Pageable 객체 덕분에 Repository가 SQL의 LIMIT/OFFSET을 자동으로 처리해줌‼️
        Page<Post> postPage = postRepository.findAll(pageable); // ⬅️ 상속받은 메서드 사용
        

        // 2️⃣ Post(Entity) Page를 PostListResponse(DTO) Page로 변환하여 반환
        // ⚠️.map() 메서드는 각 엔티티를 DTO로 변환하면서 페이징 정보(총 페이지 수, 전체 개수 등)는 그대로 유지합니다.
        return postPage.map(PostListResponse::from);
    }


}
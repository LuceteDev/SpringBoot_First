package springboot_first.pr.service.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.dto.postDTO.request.PostCreateRequest;
import springboot_first.pr.dto.postDTO.request.PostSearchRequest;
import springboot_first.pr.dto.postDTO.request.PostUpdateRequest;
import springboot_first.pr.dto.postDTO.response.PostDetailResponse;
import springboot_first.pr.dto.postDTO.response.PostListResponse;
import springboot_first.pr.entity.Post;
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException;
import springboot_first.pr.exception.ResourceNotFoundException;
import springboot_first.pr.repository.PostRepository;
import springboot_first.pr.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // 권한 확인을 위해 Objects.equals() 사용 예정

@Slf4j
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


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


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


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


    /**
     * 3️⃣ 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDetailResponse findPostById(Long postId){
        
        // 1️⃣ 게시글 ID로 조회, 없으면 예외처리 발생하기
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다. ID : " + postId));

        // 2️⃣ 응답 DTO로 변환하여 반환하기
        return PostDetailResponse.from(post);
    }


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    /**
     * 4️⃣ 게시글 수정하기 (UPDATE)
     * 인가(Authorization) 로직 포함: 요청한 사용자가 작성자인지 확인
     */
    @Transactional
    public PostDetailResponse updatePost(
        Long postId, // 1️⃣ 수정할 게시글 ID (Path Variable로 조회)
        String currentUserId, // 2️⃣ 현재 로그인 사용자 ID (Security Context/Principal에서 추출)
        PostUpdateRequest request) // 4️⃣ 수정 요청 데이터 (DTO)
    {
        // 1️⃣ 게시글 조회 (수정 대상)
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다. ID: " + postId));
            
        log.info("수정할 게시글 찾기 완료 후 post = {}", post);
        log.info("수정할 게시글 찾기 완료 후 post = {}", post.getUser());
        // 2️⃣ ⚠️ 인가(Authorization) 확인: 요청 사용자와 작성자 일치 검증
        // post.getUser().getId()는 게시글 작성자의 PK(Long)입니다.
        if (!post.getUser().getUserId().equals(currentUserId)) {
                throw new AuthenticationException("수정 권한이 없습니다. 작성자만 수정 가능합니다.");
            }
        
        // 3️⃣ 엔티티 내부의 비즈니스 메서드를 통해 데이터 변경 (Dirty Checking 활용)
        // post.update()를 호출하여 메모리상의 객체 상태만 변경
        post.update(request.getTitle(), request.getContent());
        
        // 4️⃣ 응답 DTO로 변환하여 반환 (수정된 게시글의 상세 정보)
        // @Transactional에 의해 메서드 종료 시 DB에 변경사항(title, content, updatedAt) 자동 반영됨
        return PostDetailResponse.from(post);
    }

    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    /**
     * 5️⃣ 게시글 삭제하기 (DELETE)
     * @param postId Soft Delete 대상 게시글 ID (Path Variable로 조회)
     * @param currentUserId 현재 로그인 사용자 ID (Security Context/Principal에서 추출)
     */

    @Transactional
    public void deletePost(Long postId, String currentUserId) {
    // 1️⃣ 게시글 조회
    // 💡 엔티티의 @SQLRestriction("deleted_at IS NULL") 덕분에
    // 이미 삭제된 글은 조회되지 않고 바로 Optional.empty()가 반환됩니다.
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없거나 이미 삭제되었습니다. ID: " + postId));

    // 2️⃣ 인가(Authorization) 확인: 작성자 본인인지 검증
    if (!post.getUser().getUserId().equals(currentUserId)) {
        throw new AuthenticationException("게시글 삭제 권한이 없습니다. 작성자만 삭제 가능합니다.");
    }

    // 3️⃣ Soft Delete 실행
    // 💡 실제로는 DB에서 행이 삭제되지 않고 @SQLDelete에 작성한 UPDATE 문이 실행됩니다.
    postRepository.delete(post);
    }


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    /**
     * 6️⃣ 게시판 통합 검색 (제목, 내용, 작성자 이름)
     * - 사용자가 입력한 조건이 있는 경우에만 WHERE 절에 조건이 추가됩니다.
     * - 결과는 페이징 처리되어 반환됩니다.
     */
    @Transactional(readOnly = true)
    public Page<PostListResponse> searchPosts(PostSearchRequest cond, Pageable pageable) {
        log.info("게시글 통합 검색 시작. 조건: {}, 페이지정보: {}", cond, pageable);

        // 1️⃣ 동적 쿼리 생성 (Specification 사용)
        Specification<Post> spec = (root, query, cb) -> {
            // 조건들을 담을 리스트 생성
            List<Predicate> predicates = new ArrayList<>();

            // ① 제목 검색 조건 추가 (값이 있을 때만)
            if (StringUtils.hasText(cond.getTitle())) {
                // WHERE title LIKE %검색어%
                predicates.add(cb.like(root.get("title"), "%" + cond.getTitle() + "%"));
            }

            // ② 내용 검색 조건 추가 (값이 있을 때만)
            if (StringUtils.hasText(cond.getContent())) {
                // WHERE content LIKE %검색어%
                predicates.add(cb.like(root.get("content"), "%" + cond.getContent() + "%"));
            }

            // ③ 작성자 이름(username) 검색 조건 추가 (값이 있을 때만)
            if (StringUtils.hasText(cond.getUsername())) {
                // Post 엔티티의 'user' 필드와 조인하여 'username' 필드 확인
                // WHERE user.username = '검색어'
                predicates.add(cb.equal(root.get("user").get("username"), cond.getUsername()));
            }

            // 모든 조건들을 AND로 결합 (조건이 없으면 빈 리스트이므로 전체 조회와 동일하게 동작)
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 2️⃣ 리포지토리 호출
        // 💡 Repository가 JpaSpecificationExecutor를 상속받았기에 findAll(spec, pageable) 사용 가능!
        // 💡 엔티티의 @SQLRestriction 덕분에 삭제된 글은 여기서 자동으로 제외됩니다.
        Page<Post> postPage = postRepository.findAll(spec, pageable);

        // 3️⃣ 엔티티 Page를 DTO Page로 변환하여 반환
        return postPage.map(PostListResponse::from);
    }   


    
}
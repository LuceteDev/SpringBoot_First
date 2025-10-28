import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

// Repository import
import springboot_first.pr.repository.PostRepository;
import springboot_first.pr.repository.UserRepository;

// DTO import
import springboot_first.pr.dto.PostRequestDTO;
import springboot_first.pr.dto.PostResponseDTO;

// Entity import
import springboot_first.pr.model.Post;
import springboot_first.pr.model.User;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponseDTO createPost(PostRequestDTO dto, String userEmail) {
        // 유효성 검사
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new PostValidationException("제목을 입력해주세요.");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new PostValidationException("내용을 입력해주세요.");
        }

        // 작성자 확인
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("작성자 정보를 찾을 수 없습니다."));

        // 엔티티 생성
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .build();

        // 저장
        Post saved = postRepository.save(post);
        return PostResponseDTO.fromEntity(saved);
    }

    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .filter(p -> !p.isDeleted())
                .map(PostResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}

// Spring Web
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

// Spring Security
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// Spring Framework
import org.springframework.http.ResponseEntity;

// Lombok
import lombok.RequiredArgsConstructor;

// Java
import java.util.List;


@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173")  // React 포트 명시해주기
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        PostResponseDTO response = postService.createPost(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        List<PostResponseDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}

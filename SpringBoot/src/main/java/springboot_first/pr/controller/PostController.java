// Spring Web
import org.springframework.web.bind.annotation.*;

// Spring Security
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// Spring Framework
import org.springframework.http.ResponseEntity;

// Lombok
import lombok.RequiredArgsConstructor;

// Java
import java.util.List;

// Https
import org.springframework.http.HttpStatus;

import springboot_first.pr.service.PostService;
import springboot_first.pr.dto.PostRequestDTO;
import springboot_first.pr.dto.PostResponseDTO;
import springboot_first.pr.config.CustomUserDetails;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) 
            {

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

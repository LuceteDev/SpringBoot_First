package springboot_first.pr.dto;

import lombok.Getter;
import lombok.Builder;
import java.time.LocalDateTime;
import springboot_first.pr.model.Post;



@Getter @Builder
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
    private int views;

    public static PostResponseDTO fromEntity(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorUsername(post.getAuthor().getUsername())
                .createdAt(post.getCreatedAt())
                .views(post.getViews())
                .build();
    }
}

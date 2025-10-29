package springboot_first.pr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostRequestDTO {
    private String title;
    private String content;
}

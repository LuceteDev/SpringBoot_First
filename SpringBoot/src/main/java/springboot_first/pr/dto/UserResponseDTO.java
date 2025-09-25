// src/main/java/springboot_first/pr/dto/UserResponseDTO.java

package springboot_first.pr.dto;

import lombok.Builder;
import lombok.Getter;
import springboot_first.pr.model.User;

@Getter
@Builder
public class UserResponseDTO {
    // 클라이언트에 노출해도 되는 필드만 포함합니다. (비밀번호 제외)
    private Integer id;
    private String userId;
    private String email;
    private String username;
    private String address;
    private String phoneNumber;

    // User 엔티티를 DTO로 변환하는 팩토리 메서드 (현업 표준)
    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .address(user.getAddress())
            .phoneNumber(user.getPhoneNumber())
            .build();
    }
}
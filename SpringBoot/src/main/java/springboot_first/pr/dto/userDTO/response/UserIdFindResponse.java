package springboot_first.pr.dto.userDTO.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

// ID 찾기 응답 DTO. 마스킹된 사용자 ID를 반환합니다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdFindResponse {

    private String maskedUserId; // 마스킹된 사용자 ID (예: j*****)
    private String message;      // 사용자에게 보여줄 안내 메시지


}
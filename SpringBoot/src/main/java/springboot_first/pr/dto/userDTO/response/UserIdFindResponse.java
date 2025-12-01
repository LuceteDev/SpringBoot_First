package springboot_first.pr.dto.userDTO.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;

// ID 찾기 응답 DTO. 마스킹된 사용자 ID를 반환합니다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UserIdFindResponse {

    private String maskedUserId; // 마스킹된 사용자 ID (예: j*****)
    private String message;      // 사용자에게 보여줄 안내 메시지

    /**
     * 💡 [from 메서드] 작성 위치:
     * DTO의 역할은 단순히 데이터를 담는 것이 아니라, 
     * DB Entity를 받아서 외부에 노출될 응답 객체(DTO)로 '변환'하는 책임도 가집니다.
     * 따라서 DTO 클래스 내부에 from(Entity entity) 스태틱 메서드를 작성하는 것이 가장 일반적이고 좋습니다.
     */

    /**
     * DTO의 from 메서드: User 엔티티를 받아서 응답 DTO로 변환합니다.
     * @param user 조회된 User 엔티티
     * @return 마스킹된 ID가 담긴 응답 DTO
     */
    public static UserIdFindResponse from(User user) { // 💡 매개변수로 User 엔티티를 받습니다.
        log.debug("UserIdFindResponse from() 메서드 호출, User Entity -> DTO 변환 시작");

        String originalId = user.getUserId();
        
        // 1. ID 마스킹 로직
        // ID의 첫 글자만 노출하고 나머지는 '*'로 마스킹합니다. (예: t******)
        String maskedId;
        if (originalId == null || originalId.isEmpty()) {
            maskedId = "N/A";
        } else {
            maskedId = originalId.substring(0, 1) + "*".repeat(originalId.length() - 1);
        }
        
        // 2. UserIdFindResponse의 빌더를 사용하여 필드를 채웁니다.
        return UserIdFindResponse.builder()
                // 💡 DTO의 필드인 maskedUserId에 마스킹된 값을 설정
                .maskedUserId(maskedId) 
                .message("성공적으로 회원님의 ID를 찾았습니다. 마스킹된 ID를 확인해주세요.") // 사용자 친화적 메시지 추가
                .build();
    }
}

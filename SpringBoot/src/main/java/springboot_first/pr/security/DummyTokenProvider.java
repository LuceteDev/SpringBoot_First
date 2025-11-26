package springboot_first.pr.security;

import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.entity.User;
import org.springframework.stereotype.Service;

// 💡 [초보자 실습용] 실제 JWT 서명 없이 토큰 생성 행위만 흉내내는 임시 구현체
// Spring Bean으로 등록하여 AuthService에서 주입받을 수 있게 @Service 어노테이션을 붙입니다.
@Slf4j
@Service 
public class DummyTokenProvider implements TokenProvider {

    // ⚠️ 경고: 실제 JWT 로직이 아닙니다. 구현이 완료되면 실제 JWT 라이브러리로 대체해야 합니다.
    @Override
    public String createToken(User user) {
        
        // userId와 현재 시간을 조합하여 토큰인 척하는 문자열 생성
        // "Bearer."는 토큰의 종류(인증 유형)를 나타내는 현업 표준 접두사입니다.
        String dummyToken = "Bearer." + user.getUserId() + "." + System.currentTimeMillis();
        
        log.info("임시 토큰 생성 완료. UserId: {}, Token: {}", user.getUserId(), dummyToken);
        
        return dummyToken;
    }
}
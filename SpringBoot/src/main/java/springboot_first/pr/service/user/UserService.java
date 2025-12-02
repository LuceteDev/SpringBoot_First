package springboot_first.pr.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.dto.userDTO.request.UserPasswordChangeRequest;
import springboot_first.pr.dto.userDTO.response.UserPasswordChangeResponse;
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException;
import springboot_first.pr.repository.UserRepository;

@Slf4j
@Service // 1️⃣ 서비스 선언하기
@RequiredArgsConstructor // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함

public class UserService {

 // 3️⃣ 리포지터리 객체 주입
 private final UserRepository userRepository;
 private final PasswordEncoder passwordEncoder;

 // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 사용자 정보 변경/조회 로직 구현하기 ✅ 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

 // 4️⃣ 트랜잭션 선언 후 메서드 정의하기
 @Transactional
 public UserPasswordChangeResponse changePassword(String authenticatedUserId, UserPasswordChangeRequest requestDto){
  // 메서드 시작 로그
  log.info("=== 비밀번호 변경 서비스 시작. 인증된 사용자 ID: {} ===", authenticatedUserId);
  
  // 1️⃣ 인증된 ID로 사용자 엔티티를 찾습니다.
  User user = userRepository.findByUserId(authenticatedUserId)
   .orElseThrow(() -> {
    log.error("비밀번호 변경 실패: ID '{}'에 해당하는 사용자를 찾을 수 없습니다.", authenticatedUserId);
    // 이 예외는 보통 발생하지 않지만, 사용자 세션이 유효하지 않을 때를 대비합니다.
    return new AuthenticationException("인증된 사용자 정보를 찾을 수 없습니다.");
   });
  
  log.info("사용자 ID '{}' 검색 성공. 기존 비밀번호 확인 진행.", authenticatedUserId);

  // 2️⃣ 기존 비밀번호 일치 여부 검증 (보안상 필수 로직)
  if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
   log.error("비밀번호 변경 실패: 사용자 ID '{}'의 기존 비밀번호 불일치.", authenticatedUserId);
   throw new AuthenticationException("기존 비밀번호가 일치하지 않습니다.");
  }
  log.info("기존 비밀번호 일치 확인 완료.");

  // 3️⃣ 새 비밀번호를 안전하게 암호화(해싱)합니다.
  String newEncodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
  log.debug("새 비밀번호 인코딩 완료.");

  // 4️⃣ 엔티티의 비밀번호를 업데이트합니다.
  user.setPassword(newEncodedPassword);

  log.info("=== 비밀번호 변경 성공 및 트랜잭션 커밋 준비 완료 (ID: {}) ===", user.getUserId());
  
  // 5️⃣ 성공 응답을 반환합니다. (DTO의 정적 헬퍼 메서드 사용으로 변경)
  return UserPasswordChangeResponse.success();
  }
  
}
package springboot_first.pr.service.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.entity.User;
import springboot_first.pr.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;


@Service // 1️⃣ 서비스 선언하기
@RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
public class AuthService {

  // 3️⃣ 리포지터리 객체 주입 : final로 선언해야 @RequiredArgsConstructor가 생성자를 통해 주입해 줌‼️
  private final UserRepository userRepository;

  // 💡 비밀번호 암호화를 위한 객체 주입 -> 엔티티 from 메서드로 전달
  // private final BCryptPasswordEncoder passwordEncoder; // 👈 BCryptPasswordEncoder 객체 주입 (Configuration 필요)
  private final PasswordEncoder passwordEncoder;

  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

  // 4️⃣ 트랜잭션 선언 후 메서드 정의하기
  @Transactional
  public UserRegisterResponse register(UserRegisterRequest requestDto){

    // 5️⃣ 유효성 검사 (중복 사용자 체크)
    // 아이디, 이메일, 전화번호 중복을 모두 체크
    if (userRepository.findByUserId(requestDto.getUserId()).isPresent()) {
        throw new IllegalArgumentException("회원가입 실패: 이미 존재하는 사용자 ID입니다.");
    }
    if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
        throw new IllegalArgumentException("회원가입 실패: 이미 가입된 이메일입니다.");
    }
    if (userRepository.findByPhoneNumber(requestDto.getPhoneNumber()).isPresent()) {
        throw new IllegalArgumentException("회원가입 실패: 이미 가입된 전화번호입니다.");
    }

    // 6️⃣ 중복 없을 경우 pw 암호화 해서 저장 후 User 엔티티로 반환 하기 
    String encodePassword = passwordEncoder.encode(requestDto.getPassword());
    User newUser = User.from(requestDto, encodePassword);

    // 7️⃣ DB 저장 및 상태 응답
    User savedUser = userRepository.save(newUser);
    
    return UserRegisterResponse.from(savedUser);
    // 8️⃣ 응답 DTO에 from 메서드 정의하러 가기 -> 코드 자동 생성 이용

  }
}

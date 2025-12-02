package springboot_first.pr.service.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.dto.userDTO.request.UserIdFindRequest;
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.entity.User;
import springboot_first.pr.repository.UserRepository;
import springboot_first.pr.security.TokenProvider;

import org.springframework.security.crypto.password.PasswordEncoder;

import springboot_first.pr.exception.AuthenticationException;

@Slf4j // Service 로직의 흐름을 확인하는 로깅 추가
@Service // 1️⃣ 서비스 선언하기
@RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
public class AuthService {

  // 3️⃣ 리포지터리 객체 주입 : final로 선언해야 @RequiredArgsConstructor가 생성자를 통해 주입해 줌‼️
  private final UserRepository userRepository;

  // 회원가입시에 사용될 고정 이메일 도메인
  private static final String FIXED_EMAIL_DOMAIN = "@email.com";

  // 💡 비밀번호 암호화를 위한 객체 주입 -> 엔티티 from 메서드로 전달
  // private final BCryptPasswordEncoder passwordEncoder; // 👈 BCryptPasswordEncoder 객체 주입 (Configuration 필요)
  private final PasswordEncoder passwordEncoder;

  // 로그인시에 사용될 토근 주입
  private final TokenProvider tokenProvider; 


  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 회원가입 로직 시작 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

  // 4️⃣ 트랜잭션 선언 후 메서드 정의하기
  @Transactional
  public UserRegisterResponse register(UserRegisterRequest requestDto){
    // 5️⃣ 유효성 검사 (중복 사용자 체크)

    
    String userId = requestDto.getUserId();
    // 1. 서비스 계층에서 최종 이메일 주소 구성 (비즈니스 로직)
    String fullEmail = userId + FIXED_EMAIL_DOMAIN;

    log.info("회원가입 요청 시작: userEmail={}", requestDto.getUserId()); // 💡 [로깅] 요청 시작

    // 5️⃣ 유효성 검사 (중복 사용자 체크) - ✅ existsBy... 메서드를 사용하여 최적화 하기 (DB 부담을 최소화하며 존재 여부만 확인)
        // 5-1. 사용자 ID 중복 체크 (DB 부담을 최소화)
        if (userRepository.existsByUserId(userId)) {
            log.warn("중복 사용자 ID 시도 감지: {}", userId); // 💡 [로깅] 경고
            // 💡 (수정) 일관된 예외 처리 사용
            throw new AuthenticationException("회원가입 실패: 이미 존재하는 사용자 ID입니다."); 
        }
        
        // 5-2. 구성된 이메일 주소 중복 체크
        if (userRepository.existsByEmail(fullEmail)) {
            log.warn("중복 이메일 시도 감지: {}", fullEmail);
            // 💡 (수정) 일관된 예외 처리 사용
            throw new AuthenticationException("회원가입 실패: 이미 가입된 이메일입니다.");
        }
        
        // 5-3. 전화번호 중복 체크
        if (userRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            log.warn("중복 전화번호 시도 감지: {}", requestDto.getPhoneNumber());
            // 💡 (수정) 일관된 예외 처리 사용
            throw new AuthenticationException("회원가입 실패: 이미 가입된 전화번호입니다.");
        }

        // 6️⃣ 중복 없을 경우 pw 암호화 해서 저장 후 User 엔티티로 반환 하기 
        String encodePassword = passwordEncoder.encode(requestDto.getPassword());
        log.debug("비밀번호 암호화 완료"); // 💡 [로깅] 암호화 완료
        
        // 💡 구성된 fullEmail을 엔티티 팩토리 메서드로 전달
        User newUser = User.from(requestDto, encodePassword, fullEmail);

        // 7️⃣ DB 저장 및 상태 응답
        log.debug("DB 저장 요청 (User Entity 생성 완료)"); // 💡 [로깅] DB 저장 직전
        User savedUser = userRepository.save(newUser);
        log.info("회원가입 성공 및 DB 저장 완료: ID={}", savedUser.getId()); // 💡 [로깅] 최종 성공
        
        return UserRegisterResponse.from(savedUser);
    } 

  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 로그인 로직 시작 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest requestDto) {
        log.info("AuthService.login() 호출: 로그인 시도");

        // DTO에서 통합 식별자와 비밀번호 추출
        String identifier = requestDto.getEmailOrIdOrPhone(); 
        String rawPassword = requestDto.getPassword();

        // 1️⃣ 통합 식별자를 사용하여 사용자 조회 (ID, Email, Phone 순으로 Optional.or 체이닝)
        // 아이디 검색 로직을 다시 추가하여 ID, Email, Phone 세 가지 필드로 모두 검색하도록 합니다.
        Optional<User> optionalUser = userRepository.findByUserId(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .or(() -> userRepository.findByPhoneNumber(identifier));

        // 사용자가 없을 경우 예외 발생
        User user = optionalUser
             .orElseThrow(() -> {
                 log.warn("로그인 시도 실패: 식별자 {}로 사용자를 찾을 수 없습니다.", identifier);
                 return new AuthenticationException("사용자를 찾을 수 없습니다."); 
             });
        log.debug("식별자 조회 성공. UserId: {}", user.getUserId());

        // 2️⃣ 비밀번호 검증 (BCryptPasswordEncoder 사용)
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("로그인 시도 실패: UserId {} 의 비밀번호가 일치하지 않습니다.", user.getUserId());
            throw new AuthenticationException("비밀번호가 일치하지 않습니다."); 
        }
        log.debug("비밀번호 검증 성공.");

        // 3️⃣ Access Token 및 Refresh Token 발급
        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken(user);
        
        log.info("로그인 성공 및 토큰 발급 완료. UserId: {}", user.getUserId());

        // 4️⃣ Response DTO 변환 및 반환 (토큰 2개 전달)
        return UserLoginResponse.from(user, accessToken, refreshToken);
    }


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 계정 찾기 메서드 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    
    @Transactional(readOnly = true)
    public UserIdFindResponse findIdByPhoneAndUsername(UserIdFindRequest request) {
        log.info("ID 찾기 서비스 시작: phone={}, username={}", request.getPhoneNumber(), request.getUsername());
        
        // 1. Repository 호출 (성공/실패 분기점)
        Optional<User> userOptional = userRepository.findByPhoneNumberAndUsername(
            request.getPhoneNumber(), 
            request.getUsername()
        );

        // 2. 조회 결과 처리
        // 🚨 실패 시: Optional.orElseThrow()를 사용하여 값이 없으면 예외 발생 
        User foundUser = userOptional.orElseThrow(() -> {
            log.warn("ID 찾기 실패: 휴대폰 번호 또는 본명이 일치하는 회원이 없습니다.");
            
            // 💡 [Red -> Green] AuthenticationException 발생 요구 사항 충족
            throw new AuthenticationException("입력 정보와 일치하는 계정이 없습니다."); 
        });

        // 3. 성공 시: DTO로 변환하여 마스킹된 ID 반환
        // 💡 [Red -> Green] 마스킹된 응답 DTO 반환 요구 사항 충족
        return UserIdFindResponse.from(foundUser);
    }




    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 비밀번호 변경 메서드 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //






    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 비밀번호 재설정 메서드 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //
}
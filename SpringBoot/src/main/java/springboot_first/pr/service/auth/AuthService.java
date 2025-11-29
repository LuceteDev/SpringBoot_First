package springboot_first.pr.service.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
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

  // 💡 비밀번호 암호화를 위한 객체 주입 -> 엔티티 from 메서드로 전달
  // private final BCryptPasswordEncoder passwordEncoder; // 👈 BCryptPasswordEncoder 객체 주입 (Configuration 필요)
  private final PasswordEncoder passwordEncoder;

  // 로그인시에 사용될 토근 주입
  private final TokenProvider tokenProvider; 

  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 회원가입 로직 시작 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

  // 4️⃣ 트랜잭션 선언 후 메서드 정의하기
  @Transactional
  public UserRegisterResponse register(UserRegisterRequest requestDto){
  log.info("회원가입 요청 시작: userEmail={}", requestDto.getEmail()); // 💡 [로깅] 요청 시작

    // 5️⃣ 유효성 검사 (중복 사용자 체크)
    // 아이디, 이메일, 전화번호 중복을 모두 체크 
    // if (userRepository.findByUserId(requestDto.getUserId()).isPresent()) {
    //     log.warn("중복 사용자 ID 시도 감지: {}", requestDto.getUserId()); // 💡 [로깅] 경고
    //     throw new IllegalArgumentException("회원가입 실패: 이미 존재하는 사용자 ID입니다.");
    // }
    // if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
    //     log.warn("중복 이메일 시도 감지: {}", requestDto.getEmail());
    //     throw new IllegalArgumentException("회원가입 실패: 이미 가입된 이메일입니다.");
    // }
    // if (userRepository.findByPhoneNumber(requestDto.getPhoneNumber()).isPresent()) {
    //     log.warn("중복 전화번호 시도 감지: {}", requestDto.getPhoneNumber());
    //     throw new IllegalArgumentException("회원가입 실패: 이미 가입된 전화번호입니다.");
    // }

                // 〰️〰️〰️ ⚠️ 비효율적인 이전 코드 (성능 저하) ⚠️ 〰️〰️〰️
    // findBy...()는 존재 여부만 확인하려고 해도 해당 사용자의 모든 데이터(이름, 비밀번호 등)를 DB에서 가져와 메모리(Optional)에 로드
    // 이는 존재 여부만 필요한 상황에서 불필요한 DB 부하를 유발
    

    // 5️⃣ 유효성 검사 (중복 사용자 체크) - ✅ existsBy... 메서드를 사용하여 최적화 하기 (DB 부담을 최소화하며 존재 여부만 확인)
    // if (userRepository.existsByUserId(requestDto.getUserId())) {
    //     log.warn("중복 사용자 ID 시도 감지: {}", requestDto.getUserId()); // 💡 [로깅] 경고
    //     // Spring Boot에서 RuntimeException은 ControllerAdvice로 처리하는 것이 일반적입니다.
    //     throw new RuntimeException("회원가입 실패: 이미 존재하는 사용자 ID입니다."); 
    // }
    if (userRepository.existsByEmail(requestDto.getEmail())) {
        log.warn("중복 이메일 시도 감지: {}", requestDto.getEmail());
        throw new RuntimeException("회원가입 실패: 이미 가입된 이메일입니다.");
    }
    if (userRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
        log.warn("중복 전화번호 시도 감지: {}", requestDto.getPhoneNumber());
        throw new RuntimeException("회원가입 실패: 이미 가입된 전화번호입니다.");
    }     

    // 6️⃣ 중복 없을 경우 pw 암호화 해서 저장 후 User 엔티티로 반환 하기 
    String encodePassword = passwordEncoder.encode(requestDto.getPassword());
    log.debug("비밀번호 암호화 완료"); // 💡 [로깅] 암호화 완료
    User newUser = User.from(requestDto, encodePassword);

    // 7️⃣ DB 저장 및 상태 응답
    log.debug("DB 저장 요청 (User Entity 생성 완료)"); // 💡 [로깅] DB 저장 직전
    User savedUser = userRepository.save(newUser);
    log.info("회원가입 성공 및 DB 저장 완료: ID={}", savedUser.getId()); // 💡 [로깅] 최종 성공
    
    return UserRegisterResponse.from(savedUser);
    // 8️⃣ 응답 DTO에 from 메서드 정의하러 가기 -> 코드 자동 생성 이용
    
    // ⚠️ 테스트 코드 실습용 return
    // return null;
  } 

  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 로그인 로직 시작 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    // @Transactional(readOnly = true) 
    // public UserLoginResponse login(UserLoginRequest requestDto) {
    //     log.info("AuthService.login() 호출: 로그인 시도");

    //     // ⚠️ 세 가지 다른 필드(ID, Email, Phone) 중 어느 하나만 있어도 되니까
    //     // SRP - 즉 단일 책임 원칙을 준수하게 findUserByIdentifier() priavte 메서드를 정의해서 분리한 것 ‼️

    //     // 1️⃣ 식별자를 사용하여 사용자 조회 (ID, Email, Phone 중 하나)
    //     User user = findUserByIdentifier(requestDto)
    //         .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    //     log.debug("식별자 조회 성공. UserId: {}", user.getUserId());

    //     // 2️⃣ 비밀번호 검증 (현재는 평문 비교 대신 PasswordEncoder 사용)
    //     // ⚠️ BCrypt는 단방향 해시 함수이므로, 복호화가 아닌 '입력된 비밀번호'를 다시 해시하여 '저장된 해시'와 비교해야 합니다.
        
    //     if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
    //          throw new RuntimeException("비밀번호가 일치하지 않습니다."); 
    //     }
    //     log.debug("비밀번호 검증 성공.");

    //     // 3️⃣ 토큰 발급 (DummyTokenProvider 임시 토큰 사용)
    //     String token = tokenProvider.createToken(user);
    //     log.info("로그인 성공 및 토큰 발급 완료. UserId: {}", user.getUserId());

    //     // 4️⃣ Response DTO 변환 및 반환
    //     return UserLoginResponse.from(user, token);
    // }

    // // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 로그인 Private 메서드 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    // // 💡 [현업 패턴] 3가지 식별자 중 유효한 하나를 찾기 위한 내부 로직
    // private Optional<User> findUserByIdentifier(UserLoginRequest request) {
    //     // 💠 userId가 요청에 포함되어 있다면, userId로 조회 시도
    //     if (request.getUserId() != null && !request.getUserId().isEmpty()) {
    //         return userRepository.findByUserId(request.getUserId());
    //     }
    //     // 💠 email이 요청에 포함되어 있다면, email로 조회 시도
    //     if (request.getEmail() != null && !request.getEmail().isEmpty()) {
    //         return userRepository.findByEmail(request.getEmail());
    //     }
    //     // 💠 phoneNumber가 요청에 포함되어 있다면, phoneNumber로 조회 시도
    //     if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
    //         return userRepository.findByPhoneNumber(request.getPhoneNumber());
    //     }
    //     return Optional.empty();
    // }

    // @Transactional(readOnly = true) 
    // public UserLoginResponse login(UserLoginRequest requestDto) {
    //     log.info("AuthService.login() 호출: 로그인 시도");

    //     // DTO에서 통합 식별자와 비밀번호 추출
    //     String identifier = requestDto.getEmailOrIdOrPhone(); 
    //     String rawPassword = requestDto.getPassword();
        
    //     // 1️⃣ 통합 식별자를 사용하여 사용자 조회 (ID, Email, Phone 순으로 Optional.or 체이닝)
    //     // 이 방식은 클라이언트가 보낸 단 하나의 'identifier' 값으로 세 가지 필드를 모두 검색
    //     // Optional<User> optionalUser = userRepository.findByUserId(identifier)
    //     //         .or(() -> userRepository.findByEmail(identifier))
    //     //         .or(() -> userRepository.findByPhoneNumber(identifier));
    //      // 💡 [변경] findByUserId는 제거되었으므로, 이메일을 1순위, 전화번호를 2순위로 조회합니다.
    //     Optional<User> optionalUser = userRepository.findByEmail(identifier)
    //     .or(() -> userRepository.findByPhoneNumber(identifier));
        
    //     // 사용자가 없을 경우 예외 발생
    //     User user = optionalUser
    //          .orElseThrow(() -> {
    //              log.warn("로그인 시도 실패: 식별자 {}로 사용자를 찾을 수 없습니다.", identifier);
    //              return new AuthenticationException("사용자를 찾을 수 없습니다."); 
    //          });
    //     log.debug("식별자 조회 성공. UserId: {}", user.getUserId());

    //     // 2️⃣ 비밀번호 검증 (BCryptPasswordEncoder 사용)
    //     if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
    //         log.warn("로그인 시도 실패: UserId {} 의 비밀번호가 일치하지 않습니다.", user.getUserId());
    //         throw new AuthenticationException("비밀번호가 일치하지 않습니다."); 
    //     }
    //     log.debug("비밀번호 검증 성공.");

    //     // 3️⃣ 토큰 발급
    //     // String token = tokenProvider.createToken(user);
    //     // log.info("로그인 성공 및 토큰 발급 완료. UserId: {}", user.getUserId());

    //     // // 4️⃣ Response DTO 변환 및 반환
    //     // return UserLoginResponse.from(user, token);
    //     // 3️⃣ [수정됨] Access Token 및 Refresh Token 발급
    //     // TokenProvider의 createToken() 대신 createAccessToken()과 createRefreshToken()을 호출
    //     String accessToken = tokenProvider.createAccessToken(user);
    //     String refreshToken = tokenProvider.createRefreshToken(user);
        
    //     log.info("로그인 성공 및 토큰 발급 완료. UserId: {}", user.getUserId());

    //     // 4️⃣ [수정됨] Response DTO 변환 및 반환 (토큰 2개 전달)
    //     // UserLoginResponse.from() 메서드 시그니처 변경에 맞춰 2개의 토큰을 전달
    //     return UserLoginResponse.from(user, accessToken, refreshToken);
    // }
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

}
// package springboot_first.pr.service.auth;

// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import springboot_first.pr.dto.userDTO.request.UserIdFindRequest;
// import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
// import springboot_first.pr.dto.userDTO.request.UserPasswordResetRequest;
// import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
// import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
// import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
// import springboot_first.pr.dto.userDTO.response.UserPasswordResetResponse;
// import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
// import springboot_first.pr.entity.User;
// import springboot_first.pr.repository.RefreshTokenRepository;
// import springboot_first.pr.repository.UserRepository;
// import springboot_first.pr.security.TokenProvider;
// import springboot_first.pr.entity.RefreshToken;
// import springboot_first.pr.repository.RefreshTokenRepository;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import springboot_first.pr.exception.AuthenticationException;

// @Slf4j // Service 로직의 흐름을 확인하는 로깅 추가
// @Service // 1️⃣ 서비스 선언하기
// @RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
// public class AuthService {

//   // 3️⃣ 리포지터리 객체 주입 : final로 선언해야 @RequiredArgsConstructor가 생성자를 통해 주입해 줌‼️
//   private final UserRepository userRepository;

//   // 회원가입시에 사용될 고정 이메일 도메인
//   private static final String FIXED_EMAIL_DOMAIN = "@email.com";

//   // 💡 비밀번호 암호화를 위한 객체 주입 -> 엔티티 from 메서드로 전달
//   // private final BCryptPasswordEncoder passwordEncoder; // 👈 BCryptPasswordEncoder 객체 주입 (Configuration 필요)
//   private final PasswordEncoder passwordEncoder;

//   // 로그인시에 사용될 토근 주입
//   private final TokenProvider tokenProvider; 

//   private final RefreshTokenRepository refreshTokenRepository;


//   // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 회원가입 로직 시작 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

//   // 4️⃣ 트랜잭션 선언 후 메서드 정의하기
//   @Transactional
//   public UserRegisterResponse register(UserRegisterRequest requestDto){
//     // 5️⃣ 유효성 검사 (중복 사용자 체크)

    
//     String userId = requestDto.getUserId();
//     // 1. 서비스 계층에서 최종 이메일 주소 구성 (비즈니스 로직)
//     String fullEmail = userId + FIXED_EMAIL_DOMAIN;

//     log.info("회원가입 요청 시작: userEmail={}", requestDto.getUserId()); // 💡 [로깅] 요청 시작

//     // 5️⃣ 유효성 검사 (중복 사용자 체크) - ✅ existsBy... 메서드를 사용하여 최적화 하기 (DB 부담을 최소화하며 존재 여부만 확인)
//         // 5-1. 사용자 ID 중복 체크 (DB 부담을 최소화)
//         if (userRepository.existsByUserId(userId)) {
//             log.warn("중복 사용자 ID 시도 감지: {}", userId); // 💡 [로깅] 경고
//             // 💡 (수정) 일관된 예외 처리 사용
//             throw new AuthenticationException("회원가입 실패: 이미 존재하는 사용자 ID입니다."); 
//         }
        
//         // 5-2. 구성된 이메일 주소 중복 체크
//         if (userRepository.existsByEmail(fullEmail)) {
//             log.warn("중복 이메일 시도 감지: {}", fullEmail);
//             // 💡 (수정) 일관된 예외 처리 사용
//             throw new AuthenticationException("회원가입 실패: 이미 가입된 이메일입니다.");
//         }
        
//         // 5-3. 전화번호 중복 체크
//         if (userRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
//             log.warn("중복 전화번호 시도 감지: {}", requestDto.getPhoneNumber());
//             // 💡 (수정) 일관된 예외 처리 사용
//             throw new AuthenticationException("회원가입 실패: 이미 가입된 전화번호입니다.");
//         }

//         // 6️⃣ 중복 없을 경우 pw 암호화 해서 저장 후 User 엔티티로 반환 하기 
//         String encodePassword = passwordEncoder.encode(requestDto.getPassword());
//         log.debug("비밀번호 암호화 완료"); // 💡 [로깅] 암호화 완료
        
//         // 💡 구성된 fullEmail을 엔티티 팩토리 메서드로 전달
//         User newUser = User.from(requestDto, encodePassword, fullEmail);

//         // 7️⃣ DB 저장 및 상태 응답
//         log.debug("DB 저장 요청 (User Entity 생성 완료)"); // 💡 [로깅] DB 저장 직전
//         User savedUser = userRepository.save(newUser);
//         log.info("회원가입 성공 및 DB 저장 완료: ID={}", savedUser.getId()); // 💡 [로깅] 최종 성공
        
//         return UserRegisterResponse.from(savedUser);
//     } 

//   // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 로그인 로직 시작 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

//     @Transactional(readOnly = true)
//     public UserLoginResponse login(UserLoginRequest requestDto) {
//         log.info("AuthService.login() 호출: 로그인 시도");

//         // DTO에서 통합 식별자와 비밀번호 추출
//         String identifier = requestDto.getEmailOrIdOrPhone(); 
//         String rawPassword = requestDto.getPassword();

//         // 1️⃣ 통합 식별자를 사용하여 사용자 조회 (ID, Email, Phone 순으로 Optional.or 체이닝)
//         // 아이디 검색 로직을 다시 추가하여 ID, Email, Phone 세 가지 필드로 모두 검색하도록 합니다.
//         Optional<User> optionalUser = userRepository.findByUserId(identifier)
//                 .or(() -> userRepository.findByEmail(identifier))
//                 .or(() -> userRepository.findByPhoneNumber(identifier));

//         // 사용자가 없을 경우 예외 발생
//         User user = optionalUser
//              .orElseThrow(() -> {
//                  log.warn("로그인 시도 실패: 식별자 {}로 사용자를 찾을 수 없습니다.", identifier);
//                  return new AuthenticationException("사용자를 찾을 수 없습니다."); 
//              });
//         log.debug("식별자 조회 성공. UserId: {}", user.getUserId());

//         // 2️⃣ 비밀번호 검증 (BCryptPasswordEncoder 사용)
//         if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
//             log.warn("로그인 시도 실패: UserId {} 의 비밀번호가 일치하지 않습니다.", user.getUserId());
//             throw new AuthenticationException("비밀번호가 일치하지 않습니다."); 
//         }
//         log.debug("비밀번호 검증 성공.");

//         // 3️⃣ Access Token 및 Refresh Token 발급
//         String accessToken = tokenProvider.createAccessToken(user);
//         String refreshToken = tokenProvider.createRefreshToken(user);
        
//         log.info("로그인 성공 및 토큰 발급 완료. UserId: {}", user.getUserId());

//         // 4️⃣ Response DTO 변환 및 반환 (토큰 2개 전달)
//         return UserLoginResponse.from(user, accessToken, refreshToken);
//     }


//     // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 계정 찾기 메서드 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

    
//     @Transactional(readOnly = true)
//     public UserIdFindResponse findIdByPhoneAndUsername(UserIdFindRequest request) {
//         log.info("ID 찾기 서비스 시작: phone={}, username={}", request.getPhoneNumber(), request.getUsername());
        
//         // 1. Repository 호출 (성공/실패 분기점)
//         Optional<User> userOptional = userRepository.findByPhoneNumberAndUsername(
//             request.getPhoneNumber(), 
//             request.getUsername()
//         );

//         // 2. 조회 결과 처리
//         // 🚨 실패 시: Optional.orElseThrow()를 사용하여 값이 없으면 예외 발생 
//         User foundUser = userOptional.orElseThrow(() -> {
//             log.warn("ID 찾기 실패: 휴대폰 번호 또는 본명이 일치하는 회원이 없습니다.");
            
//             // 💡 [Red -> Green] AuthenticationException 발생 요구 사항 충족
//             throw new AuthenticationException("입력 정보와 일치하는 계정이 없습니다."); 
//         });

//         // 3. 성공 시: DTO로 변환하여 마스킹된 ID 반환
//         // 💡 [Red -> Green] 마스킹된 응답 DTO 반환 요구 사항 충족
//         return UserIdFindResponse.from(foundUser);
//     }


//     // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 비밀번호 재설정 메서드 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


//     // 4️⃣ 트랜잭션 선언 후 메서드 정의하기
//     @Transactional
//     public UserPasswordResetResponse resetPassword(UserPasswordResetRequest requestDto){
//         // 메서드 시작 로그
//         log.info("=== 비밀번호 재설정 서비스 시작. 인증된 사용자 ID: {} ===", requestDto.getUserId());
        
//         // 1. Repository 호출 (성공/실패 분기점)
//         Optional<User> userOptional = userRepository.findByUserIdAndPhoneNumber(
//             requestDto.getUserId(),
//             requestDto.getPhoneNumber()
//         );

//         // 2. 조회 결과 처리 (실패 시 예외 발생)
//         User foundUser = userOptional.orElseThrow(() -> {
//             log.warn("비밀번호 재설정 실패: ID 또는 휴대폰 번호가 일치하는 회원이 없습니다. UserId: {}", requestDto.getUserId());
            
//             // 💡 변경 적용: AuthenticationException 사용
//             throw new AuthenticationException("입력 정보와 일치하는 계정이 없습니다."); 
//         });

//         // 3. 새 비밀번호를 암호화
//         String encodeNewPassword = passwordEncoder.encode(requestDto.getNewPassword());
//         log.debug("새 비밀번호 암호화 완료");

//         // 4. 기존에 DB에서 조회한 엔티티(foundUser)의 비밀번호 필드만 업데이트
//         foundUser.setPassword(encodeNewPassword); 
//         log.debug("기존 User 엔티티의 비밀번호 필드 업데이트 완료");
        
//         // 5. DB 저장 (JPA가 @Transactional에 의해 UPDATE 쿼리 실행)
//         userRepository.save(foundUser); 
//         log.info("비밀번호 재설정 성공: ID={}의 비밀번호 업데이트 완료", foundUser.getId());

//         String userId = requestDto.getUserId();
        
//         // 6. 성공 응답을 반환합니다.
//         return UserPasswordResetResponse.success(userId);
//     }


//     // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 로그아웃 메서드 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


//     @Transactional
//     public void logout(String userId) {
//         log.info("로그아웃 요청 수신. User ID: {}", userId);
        
//         // 해당 userId와 연결된 Refresh Token을 DB에서 삭제(무효화)
//         refreshTokenRepository.deleteByUserId(userId);

//         log.info("User ID: {}의 Refresh Token이 성공적으로 무효화되었습니다. 로그아웃 완료.", userId);
//     }
// }

package springboot_first.pr.service.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// DTOs
import springboot_first.pr.dto.authDTO.response.TokenRefreshResponse; // 💡 추가됨
import springboot_first.pr.dto.userDTO.request.UserIdFindRequest;
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserPasswordResetRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserPasswordResetResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;

// Entities & Repositories
import springboot_first.pr.entity.User;
import springboot_first.pr.entity.RefreshToken; 
import springboot_first.pr.repository.RefreshTokenRepository;
import springboot_first.pr.repository.UserRepository;

// Security
import springboot_first.pr.security.TokenProvider;
import springboot_first.pr.exception.AuthenticationException;


@Slf4j 
@Service 
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용으로 설정
public class AuthService {

	// 💡 의존성 주입
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider; 
	private final RefreshTokenRepository refreshTokenRepository;

	private static final String FIXED_EMAIL_DOMAIN = "@email.com";
	private static final String DEFAULT_ROLE = "USER"; // 💡 역할 상수 추가

	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 1️⃣ 회원가입 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

	@Transactional
	public UserRegisterResponse register(UserRegisterRequest requestDto){
		
		String userId = requestDto.getUserId();
		String fullEmail = userId + FIXED_EMAIL_DOMAIN;

		log.info("회원가입 요청 시작: userId={}", userId); 

		// 1. 유효성 검사 (중복 사용자 체크)
		if (userRepository.existsByUserId(userId)) {
			log.warn("중복 사용자 ID 시도 감지: {}", userId); 
			throw new AuthenticationException("회원가입 실패: 이미 존재하는 사용자 ID입니다."); 
		}
		
		if (userRepository.existsByEmail(fullEmail)) {
			log.warn("중복 이메일 시도 감지: {}", fullEmail);
			throw new AuthenticationException("회원가입 실패: 이미 가입된 이메일입니다.");
		}
		
		if (userRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
			log.warn("중복 전화번호 시도 감지: {}", requestDto.getPhoneNumber());
			throw new AuthenticationException("회원가입 실패: 이미 가입된 전화번호입니다.");
		}

		// 2. 비밀번호 암호화
		String encodePassword = passwordEncoder.encode(requestDto.getPassword());
		
		// 3. User 엔티티 생성 및 저장 (💡 DEFAULT_ROLE 전달)
		User newUser = User.from(requestDto, encodePassword, fullEmail, DEFAULT_ROLE);
		User savedUser = userRepository.save(newUser);
		
		log.info("회원가입 성공 및 DB 저장 완료: ID={}", savedUser.getId()); 
		
		return UserRegisterResponse.from(savedUser);
	} 

	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 2️⃣ 로그인 (Refresh Token 저장 포함) 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

	@Transactional // 💡 토큰 저장/갱신을 위해 @Transactional로 변경
	public UserLoginResponse login(UserLoginRequest requestDto) {
		log.info("AuthService.login() 호출: 로그인 시도");

		String identifier = requestDto.getEmailOrIdOrPhone(); 
		String rawPassword = requestDto.getPassword();

		// 1. 통합 식별자를 사용하여 사용자 조회 (ID, Email, Phone 순)
		Optional<User> optionalUser = userRepository.findByUserId(identifier)
				.or(() -> userRepository.findByEmail(identifier))
				.or(() -> userRepository.findByPhoneNumber(identifier));

		User user = optionalUser
			.orElseThrow(() -> {
				log.warn("로그인 시도 실패: 식별자 {}로 사용자를 찾을 수 없습니다.", identifier);
				return new AuthenticationException("사용자를 찾을 수 없습니다."); 
			});

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
			log.warn("로그인 시도 실패: UserId {} 의 비밀번호가 일치하지 않습니다.", user.getUserId());
			throw new AuthenticationException("비밀번호가 일치하지 않습니다."); 
		}

		// 3. Access Token 및 Refresh Token 발급
		String accessToken = tokenProvider.createAccessToken(user);
		String refreshTokenValue = tokenProvider.createRefreshToken(user);
		
		// 4. Refresh Token 저장 또는 갱신 (💡 핵심 추가 로직)
		refreshTokenRepository.findByUserId(user.getUserId())
			.ifPresentOrElse(
				// 존재하면 토큰 값만 갱신 (엔티티의 updateToken 메서드 사용)
				token -> token.updateToken(refreshTokenValue), 
				// 존재하지 않으면 새로 생성하여 저장
				() -> {
					RefreshToken newToken = RefreshToken.of(user.getUserId(), refreshTokenValue);
					refreshTokenRepository.save(newToken);
				}
			);

		log.info("로그인 성공 및 토큰 발급 완료. UserId: {}", user.getUserId());

		// 5. Response DTO 반환
		return UserLoginResponse.from(user, accessToken, refreshTokenValue);
	}


	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 3️⃣ 토큰 재발급 (💡 새로 추가됨) 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

	@Transactional(readOnly = true)
	public TokenRefreshResponse refreshToken(String userId, String refreshToken) {
		// 1. DB에 저장된 Refresh Token 조회 및 유효성 검사
		RefreshToken storedToken = refreshTokenRepository.findByUserIdAndTokenValue(userId, refreshToken)
			.orElseThrow(() -> {
				log.warn("토큰 재발급 실패: DB에 저장된 토큰이 없거나 일치하지 않습니다. userId: {}", userId);
				return new AuthenticationException("유효하지 않거나 만료된 Refresh Token입니다.");
			});
			
		// 2. 새 Access Token 생성
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new AuthenticationException("사용자 정보를 찾을 수 없습니다."));

		String newAccessToken = tokenProvider.createAccessToken(user);
		
		log.info("Access Token 재발급 성공: userId: {}", userId);

		// 3. 응답 DTO 반환
		return TokenRefreshResponse.builder()
			.accessToken(newAccessToken)
			.tokenType("Bearer")
			.expiresIn(tokenProvider.getAccessExpirationMillis())
			.build();
	}


	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 4️⃣ 로그아웃 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

	@Transactional
	public void logout(String userId) {
		log.info("로그아웃 요청 수신. User ID: {}", userId);

		// 💡 1. RefreshTokenRepository의 deleteByUserId 메서드를 직접 호출
    // 이는 하나의 DELETE SQL 쿼리를 실행하여 해당 userId의 모든 토큰을 한 번에 삭제
    int deletedCount = refreshTokenRepository.deleteByUserId(userId);
    
    // 2. 로그 기록
    if (deletedCount > 0) {
        log.info("로그아웃 완료: User ID: {}의 Refresh Token 무효화 완료. 삭제된 토큰 수: {}", userId, deletedCount);
    } else {
        log.warn("로그아웃 경고: User ID: {}에 대해 무효화할 Refresh Token이 존재하지 않습니다.", userId);
    }
	}
	
	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 5️⃣ 계정 찾기 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

	@Transactional(readOnly = true)
	public UserIdFindResponse findIdByPhoneAndUsername(UserIdFindRequest request) {
		log.info("ID 찾기 서비스 시작: phone={}, username={}", request.getPhoneNumber(), request.getUsername());
		
		// 전화번호와 이름으로 사용자 ID 조회
		Optional<User> userOptional = userRepository.findByPhoneNumberAndUsername(
			request.getPhoneNumber(), 
			request.getUsername()
		);

		User foundUser = userOptional.orElseThrow(() -> {
			log.warn("ID 찾기 실패: 휴대폰 번호 또는 본명이 일치하는 회원이 없습니다.");
			throw new AuthenticationException("입력 정보와 일치하는 계정이 없습니다."); 
		});

		return UserIdFindResponse.from(foundUser);
	}


	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 6️⃣ 비밀번호 재설정 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

  @Transactional
	public UserPasswordResetResponse resetPassword(UserPasswordResetRequest requestDto){
		log.info("=== 비밀번호 재설정 서비스 시작. 사용자 ID: {} ===", requestDto.getUserId());
		
		// 사용자 ID와 휴대폰 번호로 사용자 인증
		Optional<User> userOptional = userRepository.findByUserIdAndPhoneNumber(
				requestDto.getUserId(),
				requestDto.getPhoneNumber()
		);

		User foundUser = userOptional.orElseThrow(() -> {
				log.warn("비밀번호 재설정 실패: ID 또는 휴대폰 번호가 일치하는 회원이 없습니다. UserId: {}", requestDto.getUserId());
				throw new AuthenticationException("입력 정보와 일치하는 계정이 없습니다."); 
		});

		// 새 비밀번호 암호화 후 엔티티 업데이트
		String encodeNewPassword = passwordEncoder.encode(requestDto.getNewPassword());
		foundUser.updatePassword(encodeNewPassword); 
		
		// 💡 [수정된 로직]: Refresh Token 삭제를 통한 모든 세션 강제 로그아웃 처리 (보안 강화)
		// 1. 해당 userId와 연결된 모든 Refresh Token을 DB에서 조회
		// List<RefreshToken> tokensToDelete = refreshTokenRepository.findAllByUserId(foundUser.getUserId());

		// if (!tokensToDelete.isEmpty()) {
		// 		// 2. 조회된 모든 토큰 엔티티를 삭제 (JpaRepository의 deleteAll 사용)
		// 		refreshTokenRepository.deleteAll(tokensToDelete);
		// 		log.info("비밀번호 재설정 성공: 기존 Refresh Token {}개 강제 삭제 완료.", tokensToDelete.size());
		// } else {
		// 		log.info("비밀번호 재설정 성공: 삭제할 Refresh Token이 없습니다.");
		// }
		    // 💡 [수정된 최적화 로직]: Refresh Token 삭제를 통한 모든 세션 강제 로그아웃 처리 (단일 쿼리)
    // RefreshTokenRepository의 @Modifying이 적용된 메서드를 호출하여 단번에 삭제합니다.
    int deletedCount = refreshTokenRepository.deleteByUserId(foundUser.getUserId());

    if (deletedCount > 0) {
        log.info("비밀번호 재설정 성공: 기존 Refresh Token {}개 강제 삭제 완료.", deletedCount);
    } else {
        log.info("비밀번호 재설정 성공: 삭제할 Refresh Token이 없습니다.");
    }
		
		log.info("비밀번호 재설정 및 세션 무효화 완료: ID={}", foundUser.getId());

		return UserPasswordResetResponse.success(requestDto.getUserId());
	}


}
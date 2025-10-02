package springboot_first.pr.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // BCryptPasswordEncoder 대신 상위 인터페이스 사용
import springboot_first.pr.model.User;
import springboot_first.pr.repository.UserRepository;
import lombok.RequiredArgsConstructor; // 생성자 자동 생성을 위해 @RequiredArgsConstructor 사용 (혹은 명시적 생성자)

import springboot_first.pr.dto.UserSignUpRequestDTO;
import springboot_first.pr.dto.UserLoginRequestDTO;
import springboot_first.pr.dto.FindIdRequestDTO;
import springboot_first.pr.dto.ResetPwRequestDTO;

import lombok.Getter;
import lombok.Setter;
import java.util.Optional;

import springboot_first.pr.exception.DuplicateResourceException;
import springboot_first.pr.exception.DuplicateEmailException;
import springboot_first.pr.exception.AuthenticationException;


// @RequiredArgsConstructor를 사용하면 final 필드에 대한 생성자가 자동 생성되어 코드가 간결해집니다.
@Getter
@Setter

@Service
@RequiredArgsConstructor 
public class AuthService {

    private final UserRepository userRepository;
    // PasswordEncoder 인터페이스를 사용하고 final로 선언하여 DI를 통해 주입받도록 합니다.
    private final PasswordEncoder passwordEncoder; 
    
        // 💡 메서드 시그니처를 Request DTO를 받도록 변경
    public User register(UserSignUpRequestDTO requestDTO) { 

        // 1️⃣ 이메일 중복 체크, DTO에서 이메일을 추출하여 사용
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            // throw new RuntimeException("이미 존재하는 이메일입니다.");
            // 예외처리 핸들러를 따로 별개로 정의 했으니까, 기본값인 Runtime -> 별도로 정의한 예외처리로 던지기
            // throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
            // throw new DuplicateResourceException("이메일이_이미_존재합니다");
            throw new DuplicateResourceException("EMAIL_ALREADY_EXISTS");
        }
        // 2️⃣ 휴대폰 번호 중복 체크
        if (userRepository.findByPhoneNumber(requestDTO.getPhoneNumber()).isPresent()) {
            // throw new DuplicateResourceException("휴대폰_번호가 이미 존재합니다");
            throw new DuplicateResourceException("PHONE_ALREADY_EXISTS");
        }
        
        // 2. DTO를 DB 저장용 User 엔티티로 변환
        User user = new User();
        user.setEmail(requestDTO.getEmail());
        user.setUsername(requestDTO.getUsername());
        user.setAddress(requestDTO.getAddress());
        user.setPhoneNumber(requestDTO.getPhoneNumber());
        
        // 3. UserId 설정
        user.setUserId(requestDTO.getEmail().split("@")[0]); 
        
        // 4. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        user.setPassword(encodedPassword);
        
        // 5. DB 저장 후 저장된 엔티티를 반환
        return userRepository.save(user);
    }


    // 로그인
    public User login(UserLoginRequestDTO loginDTO) {
        // DTO에서 필요한 값을 추출하여 사용
        String emailOrUserIdOrPhone = loginDTO.getEmailOrIdOrPhone(); 
        String rawPassword = loginDTO.getPassword();
        
        // 데이터 넘길때 마다 디버깅 함.. 로그인은 이메일만 비밀번호만 있음
        System.out.println("C login : Auth서비스 부분 받은 이메일: " + emailOrUserIdOrPhone);
        System.out.println("C login : Auth서비스 부분 받은 비밀번호: " + rawPassword);
        
        // 이메일, userId, 휴대폰 번호 3가지로 로그인 가능
        User user = userRepository.findByEmail(emailOrUserIdOrPhone)
                .or(() -> userRepository.findByUserId(emailOrUserIdOrPhone))
                .or(() -> userRepository.findByPhoneNumber(emailOrUserIdOrPhone))
                .orElseThrow(() -> new AuthenticationException("사용자를 찾을 수 없습니다."));
// 예외처리 핸들러를 따로 별개로 정의 했으니까, 기본값인 Runtime -> 별도로 정의한 예외처리로 던지기
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            // throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    // ✅ 아이디/이메일 찾기
    public User findId(FindIdRequestDTO requestDTO) {
        System.out.println("C findId : AuthController에서 받은 이름: " + requestDTO.getUsername());
        System.out.println("C findId : AuthController에서 받은 휴대폰 번호: " + requestDTO.getPhoneNumber());
        return userRepository.findByUsernameAndPhoneNumber(
                requestDTO.getUsername(),
                requestDTO.getPhoneNumber()
        ).orElseThrow(() -> new AuthenticationException("사용자를 찾을 수 없습니다."));
    }


    // ✅ 비밀번호 재설정
    public boolean resetPassword(ResetPwRequestDTO requestDTO) {
        System.out.println("C resetPassword : AuthController에서 받은 이메일: " + requestDTO.getEmail());
        System.out.println("C resetPassword : AuthController에서 받은 새로운 비밀번호: " + requestDTO.getNewPassword());

        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("등록되지 않은 이메일입니다."));
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
        return true;
    }



}
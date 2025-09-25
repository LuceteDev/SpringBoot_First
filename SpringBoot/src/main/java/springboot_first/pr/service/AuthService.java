// package springboot_first.pr.service;

// import org.springframework.stereotype.Service;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import springboot_first.pr.model.User;
// import springboot_first.pr.repository.UserRepository;
// import lombok.RequiredArgsConstructor;

// import java.util.Optional;

// @Service
// public class AuthService {

//     private final UserRepository userRepository;
//     // private final PasswordEncoder passwordEncoder; 아래가 암호화 추가하는 것!
//     private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//     public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//         this.userRepository = userRepository;
//         this.passwordEncoder = passwordEncoder;
//     }

//     // 회원가입
//     // 중복 체크, 비밀번호 암호화, 유효성 검사 같은 비즈니스 로직 담당
//     // DB에 저장은 직접 하지 않고 Repository에 위임
//     public void register(User user) {
//         if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//             throw new RuntimeException("이미 존재하는 이메일입니다.");
//         }
//         user.setUserId(user.getEmail().split("@")[0]); // 이메일 앞부분을 userId로
//         // user.setPassword(passwordEncoder.encode(user.getPassword()));
//                 // 비밀번호 암호화
//         String encodedPassword = passwordEncoder.encode(user.getPassword());
//         user.setPassword(encodedPassword);
//         userRepository.save(user);
//     }

//     // 로그인
//     public User login(String emailOrUserIdOrPhone, String rawPassword) {
//         // 이메일, userId, 휴대폰 번호 3가지로 로그인 가능
//         User user = userRepository.findByEmail(emailOrUserIdOrPhone)
//                 .or(() -> userRepository.findByUserId(emailOrUserIdOrPhone))
//                 .or(() -> userRepository.findByPhoneNumber(emailOrUserIdOrPhone))
//                 .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

//         if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
//             throw new RuntimeException("비밀번호가 일치하지 않습니다.");
//         }
//         return user;
//     }
// }

package springboot_first.pr.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // BCryptPasswordEncoder 대신 상위 인터페이스 사용
import springboot_first.pr.model.User;
import springboot_first.pr.repository.UserRepository;
import lombok.RequiredArgsConstructor; // 생성자 자동 생성을 위해 @RequiredArgsConstructor 사용 (혹은 명시적 생성자)

import java.util.Optional;

// @RequiredArgsConstructor를 사용하면 final 필드에 대한 생성자가 자동 생성되어 코드가 간결해집니다.
@Service
@RequiredArgsConstructor 
public class AuthService {

    private final UserRepository userRepository;
    // PasswordEncoder 인터페이스를 사용하고 final로 선언하여 DI를 통해 주입받도록 합니다.
    private final PasswordEncoder passwordEncoder; 
    
    // 명시적인 생성자를 사용한다면 아래와 같이 작성합니다. (위에 @RequiredArgsConstructor를 사용한다면 생략 가능)
    // public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    //     this.userRepository = userRepository;
    //     this.passwordEncoder = passwordEncoder;
    // }

    // 회원가입
    // 중복 체크, 비밀번호 암호화, 유효성 검사 같은 비즈니스 로직 담당
    // DB에 저장은 직접 하지 않고 Repository에 위임
    public void register(User user) {
        // System.out.println("회원가입 요청 데이터: " + user);
        System.out.println("AuthController에서 받은 이메일: " + user.getEmail());
        System.out.println("AuthController에서 받은 비밀번호: " + user.getPassword());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        // user.setUserId(user.getEmail().split("@")[0]); // 이메일 앞부분을 userId로
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        userRepository.save(user);
    }

    // 로그인
    public User login(String emailOrUserIdOrPhone, String rawPassword) {
        System.out.println("AuthController에서 받은 아이디,이메일,휴대폰번호: " + emailOrUserIdOrPhone);
        System.out.println("AuthController에서 받은 비밀번호: " + rawPassword);
        // 이메일, userId, 휴대폰 번호 3가지로 로그인 가능
        User user = userRepository.findByEmail(emailOrUserIdOrPhone)
                .or(() -> userRepository.findByUserId(emailOrUserIdOrPhone))
                .or(() -> userRepository.findByPhoneNumber(emailOrUserIdOrPhone))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
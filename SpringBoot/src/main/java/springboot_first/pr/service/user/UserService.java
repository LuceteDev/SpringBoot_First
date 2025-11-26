// package springboot_first.pr.service.user;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import lombok.RequiredArgsConstructor;
// import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
// import springboot_first.pr.entity.User;
// import springboot_first.pr.repository.UserRepository;

// @Service // 1️⃣ 서비스 선언하기
// @RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
// public class UserService {

//   // 3️⃣ 리포지터리 객체 주입
//   private UserRepository userRepository;

//   // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 사용자 정보 변경/조회 로직 구현하기 ✅ 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

//   // 4️⃣ 트랜잭션 선언 후 메서드 정의하기
//   @Transactional
//   public User register(Long ){

//     User user = userRepository.findByEmail(null)
//   }


// }

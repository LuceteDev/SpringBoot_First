package springboot_first.pr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot_first.pr.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email); // 이메일로 조회 가능 SELECT * FROM users WHERE email=?
    Optional<User> findByUserId(String userId);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsernameAndPhoneNumber(String username, String phoneNumber);

}

// JPA가 자동으로 SQL을 만들어 DB와 통신.

// JpaRepository<User, Integer>를 상속 → 기본적인 CRUD(저장, 조회, 수정, 삭제) 메서드 자동 제공

// 여기에 커스텀 쿼리 메서드(findByEmail)도 추가 가능

// ✅ 즉, Repository는 DB 접근 전용 계층이고, 개발자가 SQL 직접 안 써도 됩니다.
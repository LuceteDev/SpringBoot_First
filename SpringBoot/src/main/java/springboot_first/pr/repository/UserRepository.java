package springboot_first.pr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import springboot_first.pr.entity.User;
import java.util.List;


// 1️⃣ extends JpaRepository<Entity, 기본키 타입> 상속받기
public interface UserRepository extends JpaRepository<User, Long>{

  // 〰️〰️〰️ ⚠️ 로그인은 3가지 방식으로 할거니까  〰️〰️〰️ //
  // 3️⃣ @Query 어노테이션으로 직접 적은 쿼리 수행
  
  // 〰️〰️〰️ 2️⃣ findBy메서드명을 입력하면 자동으로 List<entity>로 반환하는 코드 작성 가능 〰️〰️〰️ //

  // 〰️〰️〰️ 💠 아이디 찾기 〰️〰️〰️ //
  // List<User> findByUserId(String userId); 
  // findBy...와 같은 메서드 명명 규칙(Query Method)을 통해 쿼리를 자동 생성할 수 있지만 반환형은 Optional<>로 하는 것이 좋다고 함
  Optional<User> findByUserId(String userId);


  // 〰️〰️〰️ 💠 이메일 찾기 〰️〰️〰️ //
  Optional<User> findByEmail(String email);
  

  // 〰️〰️〰️ 💠 휴대폰 찾기 〰️〰️〰️ //
  Optional<User> findByPhoneNumber(String phoneNumber);
}

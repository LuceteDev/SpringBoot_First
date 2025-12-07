package springboot_first.pr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import springboot_first.pr.entity.User;

@Repository // ✅ 리포지터리 선언하기
// 1️⃣ extends JpaRepository<Entity, 기본키 타입> 상속받기
public interface UserRepository extends JpaRepository<User, Long>{

  // ⚠️ 회원가입때 진행하는 쿼리들은 DB 부하를 줄이기 위해 boolean 을 사용할 것 ⚠️ //
  // boolean existsByUserId(String userId);
  // boolean existsByEmail(String email);
  // boolean existsByPhoneNumber(String phoneNumber);

  // // 〰️〰️〰️ ⚠️ 로그인은 3가지 방식으로 할거니까  〰️〰️〰️ //
  // // 3️⃣ @Query 어노테이션으로 직접 적은 쿼리 수행
  
  // // 〰️〰️〰️ 2️⃣ findBy메서드명을 입력하면 자동으로 List<entity>로 반환하는 코드 작성 가능 〰️〰️〰️ //

  // // 〰️〰️〰️ 💠 아이디 찾기 〰️〰️〰️ //
  // // List<User> findByUserId(String userId); 
  // // findBy...와 같은 메서드 명명 규칙(Query Method)을 통해 쿼리를 자동 생성할 수 있지만 반환형은 Optional<>로 하는 것이 좋다고 함
  // Optional<User> findByUserId(String userId);

  // // 〰️〰️〰️ 💠 이메일 찾기 〰️〰️〰️ //
  // Optional<User> findByEmail(String email);
  
  // // 〰️〰️〰️ 💠 휴대폰 찾기 〰️〰️〰️ //
  // Optional<User> findByPhoneNumber(String phoneNumber);

  // // 〰️〰️〰️ 💠 계정(이메일) 찾기에 사용 〰️〰️〰️ //
  // Optional<User> findByPhoneNumberAndUsername(String phoneNumber, String username);

  // // 〰️〰️〰️ 💠 비밀번호 재설정에 사용 ✅ 〰️〰️〰️ //
  // // 사용자 ID와 휴대폰 번호가 일치하는 계정을 찾아 신원 확인 (비밀번호 변경 전 검증 단계)
  // Optional<User> findByUserIdAndPhoneNumber(String userId, String phoneNumber);

  // Soft Delete 적용: deletedAt이 NULL인(삭제되지 않은) 사용자만 체크
  // ✅ user 엔티티에 @Where(clause = "deleted_at IS NULL")를 사용하면 좀 더 간단!
  // 이 엔티티를 조회하는 모든 쿼리에 이 조건이 자동 추가됨
  
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.userId = :userId AND u.deletedAt IS NULL")
  boolean existsByUserId(String userId);
  
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
  boolean existsByEmail(String email);
  
  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.phoneNumber = :phoneNumber AND u.deletedAt IS NULL")
  boolean existsByPhoneNumber(String phoneNumber);


  // 〰️〰️〰️ 💠 아이디 찾기 (Soft Delete 적용) 〰️〰️〰️ //
  @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.deletedAt IS NULL")
  Optional<User> findByUserId(String userId);


  // 〰️〰️〰️ 💠 이메일 찾기 (Soft Delete 적용) 〰️〰️〰️ //
  @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
  Optional<User> findByEmail(String email);
  

  // 〰️〰️〰️ 💠 휴대폰 찾기 (Soft Delete 적용) 〰️〰️〰️ //
  @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.deletedAt IS NULL")
  Optional<User> findByPhoneNumber(String phoneNumber);


  // 〰️〰️〰️ 💠 계정(이메일) 찾기에 사용 (Soft Delete 적용) 〰️〰️〰️ //
  @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.username = :username AND u.deletedAt IS NULL")
  Optional<User> findByPhoneNumberAndUsername(String phoneNumber, String username);


  // 〰️〰️〰️ 💠 비밀번호 재설정에 사용 (Soft Delete 적용) ✅ 〰️〰️〰️ //
  // 사용자 ID와 휴대폰 번호가 일치하는 계정을 찾아 신원 확인 (비밀번호 변경 전 검증 단계)
  @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.phoneNumber = :phoneNumber AND u.deletedAt IS NULL")
  Optional<User> findByUserIdAndPhoneNumber(String userId, String phoneNumber);


  // 💡 [추가] 회원 탈퇴 (Soft Delete) 구현을 위한 벌크 UPDATE 쿼리
  @Modifying // UPDATE 쿼리이므로 필수
  @Transactional // 쓰기 작업이므로 필수,⚠️ Repository의 벌크(Bulk) 연산에는 필수
  @Query("UPDATE User u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.userId = :userId AND u.deletedAt IS NULL")
  int softDeleteByUserId(String userId); 
}


package springboot_first.pr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import springboot_first.pr.entity.RefreshToken;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	// ✅ 1. 토큰 무효화 (로그아웃, 비밀번호 재설정, 회원 탈퇴 시 사용) 해당 유저의 모든 Refresh Token을 무효화
  @Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.userId = :userId") // 💡 JPQL 쿼리 명시
	int deleteByUserId(String userId); // return 타입은 삭제된 row 수 (int)
  
	// ✅ 1. 처음 로그아웃 구현시 위 deleteByUserId 에 @Query 를 사용하지 않아서 임시로 list<>형태로 사용
	// List<RefreshToken> findAllByUserId(String userId);

	// ✅ 2. 로그인 시 Refresh Token을 저장하거나 갱신할 때 기존 토큰이 있는지 조회
	Optional<RefreshToken> findByUserId(String userId); 

	// ✅ 3. Refresh Token 값 자체로 토큰 엔티티 조회 (일반적인 토큰 유효성 검사에 사용)
	Optional<RefreshToken> findByTokenValue(String tokenValue);
	
	// 💡 4. 토큰 재발급 시 (Refresh) 유저 ID와 토큰 값 두 가지로 정확히 조회 (보안 강화)
	// (AuthService의 refreshToken 메서드에서 사용됩니다.)
	Optional<RefreshToken> findByUserIdAndTokenValue(String userId, String tokenValue);

}
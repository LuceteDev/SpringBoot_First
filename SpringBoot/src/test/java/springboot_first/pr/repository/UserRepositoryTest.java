package springboot_first.pr.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException; // 💡 (필수) DB 제약 조건 위반 예외
import org.springframework.test.context.ActiveProfiles;
import springboot_first.pr.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat; // 💡 (필수) AssertJ 사용 (Optional, 객체 비교)
import static org.junit.jupiter.api.Assertions.assertThrows; // 💡 (필수) JUnit5 assertThrows 사용 (예외 검증)
import static org.junit.jupiter.api.Assertions.assertTrue; // 💡 (필수) JUnit5 assertTrue 사용 (boolean 검증)
import static org.junit.jupiter.api.Assertions.assertFalse; // 💡 (필수) JUnit5 assertFalse 사용

@DataJpaTest // 💡 (고정) JPA 관련 빈만 로드하여 실제 DB 연결 테스트
@ActiveProfiles("test") 
@DisplayName("Repository 테스트: UserRepository - 데이터 접근 및 무결성 검증")
class UserRepositoryTest {

    // 1️⃣ 테스트할 Repository 빈을 주입받습니다.
    @Autowired 
    private UserRepository userRepository;

    // 2️⃣ 헬퍼 메서드: 테스트용 User 엔티티 생성 및 저장
    private User createAndSaveTestUser(String email, String phoneNumber, String username) { 
        // 이메일에서 ID를 추출하여 userId로 사용 (엔티티의 from() 메서드 로직과 동일하게)
        String userId = email.substring(0, email.indexOf("@"));                  
        User user = User.builder()
                .userId(userId)
                .email(email)
                .username(username)
                .password("encoded_password1234!")
                .phoneNumber(phoneNumber)
                .build();
        return userRepository.save(user); // DB에 저장하고 반환
    }

    // =================================================================================
    // 1. 저장 및 조회 기본 테스트
    // =================================================================================
    // 💡 Repository 메서드: JpaRepository.findById(Long id)
    @Test
    @DisplayName("기본_저장_조회: 회원을 저장하고 ID로 성공적으로 조회해야 한다.")
    void save_and_find_by_id_success() {
        // given
        User savedUser = createAndSaveTestUser("save@test.com", "010-9876-5432", "테스트저장");

        // when
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo("save");
    }

    // =================================================================================
    // 2. ID 찾기 (findByPhoneNumberAndUsername) 테스트
    // =================================================================================
    // 💡 Repository 메서드: Optional<User> findByPhoneNumberAndUsername(String phoneNumber, String username)
    @Test
    @DisplayName("ID찾기_성공: 휴대폰 번호와 본명이 일치하면 회원을 성공적으로 조회해야 한다.")
    void find_by_phone_and_username_success() {
        // given (준비): 찾을 유저 저장
        createAndSaveTestUser("findid@test.com", "010-5555-5555", "검색자"); 

        // when
        Optional<User> foundUser = userRepository.findByPhoneNumberAndUsername("010-5555-5555", "검색자"); 

        // then (검증)
        assertThat(foundUser).isPresent(); 
        assertThat(foundUser.get().getUserId()).isEqualTo("findid"); 
    }
    
    // 💡 Repository 메서드: Optional<User> findByPhoneNumberAndUsername(String phoneNumber, String username)
    @Test
    @DisplayName("ID찾기_실패: 휴대폰 번호만 일치하고 본명이 다르면 조회되지 않아야 한다.")
    void find_by_phone_and_username_fail_mismatch_username() {
        // given (준비): 유저 저장
        createAndSaveTestUser("mismatch@test.com", "010-6666-6666", "진짜이름"); 

        // when
        Optional<User> foundUser = userRepository.findByPhoneNumberAndUsername("010-6666-6666", "가짜이름"); 

        // then (검증)
        assertThat(foundUser).isEmpty(); // 결과가 없어야 함
    }
    
    // =================================================================================
    // 3. 존재 확인 (existsBy...) 테스트
    // =================================================================================
    
    // 💡 Repository 메서드: boolean existsByEmail(String email)
    @Test
    @DisplayName("존재확인_성공: [이메일]로 검색하면 True를 반환해야 한다")
    void exists_by_email_success() {
        // given (준비): 이메일을 가진 유저를 저장
        createAndSaveTestUser("exist@email.com", "010-1111-1111", "테스터1"); 

        // when & then
        assertTrue(userRepository.existsByEmail("exist@email.com"), "저장된 이메일은 존재해야 합니다.");
        assertFalse(userRepository.existsByEmail("non_exist@test.com"), "없는 이메일은 존재하지 않아야 합니다.");
    }
    
    // 💡 Repository 메서드: boolean existsByPhoneNumber(String phoneNumber)
    @Test
    @DisplayName("존재확인_성공: [전화번호]로 검색하면 True를 반환해야 한다")
    void exists_by_phone_number_success() {
        // given (준비): 전화번호를 가진 유저를 저장
        createAndSaveTestUser("exist2@email.com", "010-2222-2222", "테스터2"); 

        // when & then
        assertTrue(userRepository.existsByPhoneNumber("010-2222-2222"), "저장된 전화번호는 존재해야 합니다.");
        assertFalse(userRepository.existsByPhoneNumber("010-9999-9999"), "없는 전화번호는 존재하지 않아야 합니다.");
    }


    // =================================================================================
    // 4. DB 제약 조건 위반 테스트 (DataIntegrityViolationException)
    // =================================================================================

    // 💡 Repository 메서드: JpaRepository.save() (DB 제약조건인 email unique 위반 테스트)
    @Test
    @DisplayName("DB제약조건_실패: Unique 필드인 [email]이 중복되면 예외가 발생해야 한다")
    void save_fail_email_duplication() {
        // given (준비): 첫 번째 사용자 저장
        createAndSaveTestUser("duplicate@email.com", "010-1111-1111", "유저1");

        // 🚨 email이 중복되는 두 번째 User 엔티티 생성
        User duplicateUser = User.builder()
                .userId("different_id") 
                .email("duplicate@email.com") // ⬅️ 중복 이메일
                .username("유저2")
                .password("pass")
                .phoneNumber("010-2222-2222")
                .build();

        // when & then (실행 시 예외 검증)
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicateUser); // saveAndFlush를 사용하여 즉시 DB에 반영 시도
        });
    }

    // 💡 Repository 메서드: JpaRepository.save() (DB 제약조건인 phone_number unique 위반 테스트)
    @Test
    @DisplayName("DB제약조건_실패: Unique 필드인 [phoneNumber]가 중복되면 예외가 발생해야 한다")
    void save_fail_phone_number_duplication() {
        // given (준비): 첫 번째 사용자 저장
        createAndSaveTestUser("test1@email.com", "010-3333-4444", "유저1");

        // 🚨 phoneNumber가 중복되는 두 번째 User 엔티티 생성
        User duplicateUser = User.builder()
                .userId("test2_id")
                .email("test2@email.com")
                .username("유저2")
                .password("pass")
                .phoneNumber("010-3333-4444") // ⬅️ 중복 전화번호
                .build();

        // when & then 
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicateUser); 
        });
    }
}
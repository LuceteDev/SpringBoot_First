package springboot_first.pr.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import springboot_first.pr.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * UserRepository 통합 테스트
 * - 테스트는 메모리 DB에서 실행되며, 각 테스트 종료 후 데이터는 롤백됩니다.
 */
@DataJpaTest
@ActiveProfiles("test") 
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // 테스트에 사용할 User 엔티티를 생성하고 DB에 저장하는 헬퍼 메서드
    private User createAndSaveTestUser(String userId, String email, String phoneNumber) {
        User user = User.builder()
                .userId(userId)
                .email(email)
                .username("테스트유저")
                .password("encoded_password1234!")
                .phoneNumber(phoneNumber)
                .build();
        return userRepository.save(user);
    }


    // =================================================================================
    // 1. 커스텀 쿼리 메서드 테스트 (중복 확인)
    // =================================================================================

    @Test
    @DisplayName("존재확인_성공: 존재하는_userId로_검색하면_true를_반환해야_한다")
    void existsByUserId_returns_true_when_user_exists() {
        // given (준비): "j"라는 ID를 가진 유저를 저장
        createAndSaveTestUser("j", "j@test.com", "010-1212-1212");

        // when & then (실행 및 검증): "j"를 체크하면 True, 없는 ID를 체크하면 False
        assertTrue(userRepository.existsByUserId("j"), "저장된 'j' ID는 존재해야 합니다.");
        assertFalse(userRepository.existsByUserId("non_existing_id"), "없는 ID는 존재하지 않아야 합니다.");
    }

    @Test
    @DisplayName("존재확인_성공: 존재하는_email로_검색하면_true를_반환해야_한다")
    void existsByEmail_returns_correctly() {
        // given (준비): "j@j.com" 이메일을 가진 유저를 저장
        createAndSaveTestUser("j", "j@j.com", "010-1212-1212");

        // when & then
        assertTrue(userRepository.existsByEmail("j@j.com"), "저장된 이메일은 존재해야 합니다.");
        assertFalse(userRepository.existsByEmail("non_existing@test.com"), "없는 이메일은 존재하지 않아야 합니다.");
    }

    @Test
    @DisplayName("존재확인_성공: 존재하는_phoneNumber로_검색하면_true를_반환해야_한다")
    void existsByPhoneNumber_returns_correctly() {
        // given (준비): "010-1212-1212" 전화번호를 가진 유저를 저장
        createAndSaveTestUser("j", "j@j.com", "010-1212-1212");

        // when & then
        assertTrue(userRepository.existsByPhoneNumber("010-1212-1212"), "저장된 전화번호는 존재해야 합니다.");
        assertFalse(userRepository.existsByPhoneNumber("010-9999-9999"), "없는 전화번호는 존재하지 않아야 합니다.");
    }
    
    // =================================================================================
    // 2. Optional 반환 쿼리 메서드 테스트 (로그인/조회)
    // =================================================================================

    @Test
    @DisplayName("조회_성공: findByUserId로_회원을_성공적으로_조회하고_Optional_을_반환해야_한다")
    void findByUserId_success() {
        // given (준비): "j" ID를 가진 유저 저장
        createAndSaveTestUser("j", "j@j.com", "010-1212-1212");
        
        // when (실행)
        Optional<User> foundUser = userRepository.findByUserId("j");
        Optional<User> notFoundUser = userRepository.findByUserId("non_existent");
        
        // then (검증)
        assertThat(foundUser).isPresent(); // "j"는 존재해야 함
        assertThat(foundUser.get().getEmail()).isEqualTo("j@j.com"); // 데이터가 일치하는지 확인
        assertThat(notFoundUser).isEmpty(); // 없는 ID는 Optional.empty()여야 함
    }
    
    @Test
    @DisplayName("조회_성공: findByEmail로_회원을_성공적으로_조회해야_한다")
    void findByEmail_success() {
        // given
        createAndSaveTestUser("j", "j@j.com", "010-1212-1212");

        // when
        Optional<User> foundUser = userRepository.findByEmail("j@j.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo("j");
    }

    @Test
    @DisplayName("조회_성공: findByPhoneNumber로_회원을_성공적으로_조회해야_한다")
    void findByPhoneNumber_success() {
        // given
        createAndSaveTestUser("j", "j@j.com", "010-1212-1212");

        // when
        Optional<User> foundUser = userRepository.findByPhoneNumber("010-1212-1212");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("j@j.com");
    }

    // =================================================================================
    // 3. DB 제약 조건 테스트 (중복 저장 실패)
    // =================================================================================
    
    @Test
    @DisplayName("DB제약조건_실패: userId가_중복되면_예외가_발생해야_한다")
    void save_fail_due_to_duplicate_userId() {
        // given
        createAndSaveTestUser("duplicate_id", "test1@test.com", "010-1111-1111");
        User user2 = User.builder()
                .userId("duplicate_id") // 🚨 userId 중복
                .email("test2@test.com")
                .username("유저2")
                .password("pass")
                .phoneNumber("010-2222-2222")
                .build();

        // when & then: user2 저장 시도 시 DataIntegrityViolationException 발생 예상
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("DB제약조건_실패: email이_중복되면_예외가_발생해야_한다")
    void save_fail_due_to_duplicate_email() {
        // given
        createAndSaveTestUser("id1", "duplicate@email.com", "010-1111-1111");
        User user2 = User.builder()
                .userId("id2")
                .email("duplicate@email.com") // 🚨 email 중복
                .username("유저2")
                .password("pass")
                .phoneNumber("010-2222-2222")
                .build();

        // when & then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("DB제약조건_실패: phoneNumber가_중복되면_예외가_발생해야_한다")
    void save_fail_due_to_duplicate_phoneNumber() {
        // given
        createAndSaveTestUser("id1", "test1@test.com", "010-3333-4444");
        User user2 = User.builder()
                .userId("id2")
                .email("test2@test.com")
                .username("유저2")
                .password("pass")
                .phoneNumber("010-3333-4444") // 🚨 phoneNumber 중복
                .build();

        // when & then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
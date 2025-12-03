package springboot_first.pr.repository;

import org.junit.jupiter.api.BeforeEach;
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

@DataJpaTest
@ActiveProfiles("test") 
@DisplayName("Repository 테스트: UserRepository - 모든 쿼리 메서드 및 무결성 검증")
class UserRepositoryTest {

    @Autowired 
    private UserRepository userRepository;

    // 📌 테스트에 사용할 고정 데이터 상수
    private static final String P_USER_ID = "primary_user";
    private static final String P_PHONE_NUMBER = "010-1234-5678";
    private static final String P_EMAIL = "primary@test.com";
    private static final String P_USERNAME = "기본사용자";

    // 💡 모든 테스트에서 사용할 기준 유저 엔티티
    private User primaryUser;

    /**
     * 모든 @Test 메서드 실행 전에 기준 User를 DB에 저장하고 트랜잭션 롤백으로 격리합니다.
     */
    @BeforeEach
    void setUp() {
        // DB에 저장될 기준 유저
        primaryUser = User.builder()
                .userId(P_USER_ID)
                .email(P_EMAIL)
                .username(P_USERNAME)
                .password("encoded_password1234!")
                .phoneNumber(P_PHONE_NUMBER)
                .build();
        userRepository.save(primaryUser);
    }
    
    // =================================================================================
    // 1️⃣ 기본 CRUD
    // =================================================================================

    // 💡 Repository 메서드: JpaRepository.findById(Long id)
    @Test
    @DisplayName("기본_저장_조회: 회원을 저장하고 ID(PK)로 성공적으로 조회해야 한다.")
    void save_and_find_by_id_success() {
        // when
        Optional<User> foundUser = userRepository.findById(primaryUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo(P_USER_ID);
        assertThat(foundUser.get().getUsername()).isEqualTo(P_USERNAME);
    }
    
    // =================================================================================
    // 2️⃣ ID 찾기 (findByPhoneNumberAndUsername)
    // =================================================================================

    // 💡 Repository 메서드: Optional<User> findByPhoneNumberAndUsername(String phoneNumber, String username)
    @Test
    @DisplayName("ID찾기_성공: 휴대폰 번호와 본명이 일치하면 회원을 성공적으로 조회해야 한다.")
    void find_by_phone_and_username_success() {
        // when
        Optional<User> foundUser = userRepository.findByPhoneNumberAndUsername(P_PHONE_NUMBER, P_USERNAME); 

        // then (검증)
        assertThat(foundUser).isPresent(); 
        assertThat(foundUser.get().getUserId()).isEqualTo(P_USER_ID); 
    }
    
    @Test
    @DisplayName("ID찾기_실패: 휴대폰 번호만 일치하고 본명이 다르면 조회되지 않아야 한다.")
    void find_by_phone_and_username_fail_mismatch_username() {
        // when
        Optional<User> foundUser = userRepository.findByPhoneNumberAndUsername(P_PHONE_NUMBER, "가짜이름"); 

        // then (검증)
        assertThat(foundUser).isEmpty(); // 결과가 없어야 함
    }
    
    // =================================================================================
    // 3️⃣ 단일 사용자 조회 (로그인 및 비밀번호 변경에 사용) 테스트
    // =================================================================================

    // 💡 Repository 메서드: Optional<User> findByUserId(String userId)
    @Test
    @DisplayName("단일조회_성공: [사용자 ID]로 회원을 성공적으로 조회해야 한다.")
    void find_by_userId_success() {
        // when
        Optional<User> foundUser = userRepository.findByUserId(P_USER_ID);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(P_USERNAME);
    }

    // 💡 Repository 메서드: Optional<User> findByEmail(String email)
    @Test
    @DisplayName("단일조회_성공: [이메일]로 회원을 성공적으로 조회해야 한다.")
    void find_by_email_success() {
        // when
        Optional<User> foundUser = userRepository.findByEmail(P_EMAIL);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo(P_USER_ID);
    }
    
    // 💡 Repository 메서드: Optional<User> findByPhoneNumber(String phoneNumber)
    @Test
    @DisplayName("단일조회_성공: [전화번호]로 회원을 성공적으로 조회해야 한다.")
    void find_by_phone_number_success() {
        // when
        Optional<User> foundUser = userRepository.findByPhoneNumber(P_PHONE_NUMBER);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo(P_USER_ID);
    }
    
    // =================================================================================
    // 4️⃣ 존재 확인 (existsBy...) 성공(✅) 테스트
    // =================================================================================
    
    // 💡 Repository 메서드: boolean existsByUserId(String userId)
    @Test
    @DisplayName("존재확인: [사용자 ID]로 검색 시, 존재하는 경우 True, 없는 경우 False 반환")
    void exists_by_userId_success() {
        // when & then
        assertTrue(userRepository.existsByUserId(P_USER_ID), "저장된 사용자 ID는 존재해야 합니다.");
        assertFalse(userRepository.existsByUserId("nonexistid"), "없는 사용자 ID는 존재하지 않아야 합니다.");
    }

    // 💡 Repository 메서드: boolean existsByEmail(String email)
    @Test
    @DisplayName("존재확인: [이메일]로 검색 시, 존재하는 경우 True, 없는 경우 False 반환")
    void exists_by_email_success() {
        // when & then
        assertTrue(userRepository.existsByEmail(P_EMAIL), "저장된 이메일은 존재해야 합니다.");
        assertFalse(userRepository.existsByEmail("non_exist@test.com"), "없는 이메일은 존재하지 않아야 합니다.");
    }
    
    // 💡 Repository 메서드: boolean existsByPhoneNumber(String phoneNumber)
    @Test
    @DisplayName("존재확인: [전화번호]로 검색 시, 존재하는 경우 True, 없는 경우 False 반환")
    void exists_by_phone_number_success() {
        // when & then
        assertTrue(userRepository.existsByPhoneNumber(P_PHONE_NUMBER), "저장된 전화번호는 존재해야 합니다.");
        assertFalse(userRepository.existsByPhoneNumber("010-9999-9999"), "없는 전화번호는 존재하지 않아야 합니다.");
    }

    // =================================================================================
    // 5️⃣ 비밀번호 재설정 테스트 (findByUserIdAndPhoneNumber)
    // =================================================================================
    
    // 💡 Repository 메서드: Optional<User> findByUserIdAndPhoneNumber(String userId, String phoneNumber)
    @Test
    @DisplayName("비밀번호재설정_성공: [ID와 휴대폰 번호]가 모두 일치하면 사용자를 조회한다.")
    void findByUserIdAndPhoneNumber_Success() {
        // when
        Optional<User> foundUser = userRepository.findByUserIdAndPhoneNumber(P_USER_ID, P_PHONE_NUMBER);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo(P_USER_ID);
    }

    @Test
    @DisplayName("비밀번호재설정_실패: ID는 일치하지만 휴대폰 번호가 다르면 찾을 수 없다.")
    void findByUserIdAndPhoneNumber_Fail_WrongPhone() {
        // when
        Optional<User> foundUser = userRepository.findByUserIdAndPhoneNumber(P_USER_ID, "010-9999-9999");

        // then
        assertThat(foundUser).isNotPresent();
    }

    // =================================================================================
    // ⚠️ DB 제약 조건 위반 테스트 (DataIntegrityViolationException)
    // =================================================================================

    // 💡 Repository 메서드: JpaRepository.save() (DB 제약조건인 userId unique 위반 테스트)
    @Test
    @DisplayName("DB제약조건_실패: Unique 필드인 [userId]가 중복되면 예외가 발생해야 한다.")
    void save_fail_userId_duplication() {
        // given (primaryUser와 동일한 userId를 가진 새 유저)
        User duplicateUser = User.builder()
                .userId(P_USER_ID) // ⬅️ 중복 사용자 ID (primaryUser와 동일)
                .email("another@email.com") 
                .username("유저2")
                .password("pass")
                .phoneNumber("010-5555-5555") 
                .build();

        // when & then (실행 시 예외 검증)
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicateUser); // saveAndFlush로 즉시 DB에 반영 시도
        }, "중복된 userId 저장 시 DataIntegrityViolationException이 발생해야 합니다.");
    }
    
    // 💡 Repository 메서드: JpaRepository.save() (DB 제약조건인 email unique 위반 테스트)
    @Test
    @DisplayName("DB제약조건_실패: Unique 필드인 [email]이 중복되면 예외가 발생해야 한다")
    void save_fail_email_duplication() {
        // 🚨 email이 중복되는 두 번째 User 엔티티 생성
        User duplicateUser = User.builder()
                .userId("different_id") 
                .email(P_EMAIL) // ⬅️ 중복 이메일 (primaryUser와 동일)
                .username("유저2")
                .password("pass")
                .phoneNumber("010-2222-2222")
                .build();

        // when & then (실행 시 예외 검증)
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicateUser); 
        });
    }
}
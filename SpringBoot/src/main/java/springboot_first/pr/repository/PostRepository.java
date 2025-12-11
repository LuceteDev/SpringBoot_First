package springboot_first.pr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import springboot_first.pr.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 1️⃣ 기본 CRUD 기능은 JpaRepository 상속으로 자동 제공됨 (save, findById, findAll, delete 등)

    // 2️⃣ 게시글 목록 조회 (페이지네이션 적용)
    // - JpaRepository의 findAll(Pageable pageable) 메서드를 상속받아 사용
    // - Service 계층에서 Pageable 객체를 넘기면 Page<Post> 형태로 데이터를 반환 ‼️
    // ex) public Page<Post> findAll(Pageable pageable); // (findAll즉 상속받아 쓰므로 주석처리)

    // 〰️〰️〰️ 💠 게시글 목록 조회 〰️〰️〰️ //

    // 3️⃣ 제목이나 내용을 이용한 검색 기능 (페이지네이션 적용)
    // - 현업에서 자주 쓰는 쿼리 메소드 (제목에 특정 문자열이 포함된 게시글 검색)
    Page<Post> findByTitleContaining(String title, Pageable pageable);
    

    // 〰️〰️〰️ 💠 게시글 검색 〰️〰️〰️ //

    // 4️⃣ 작성자 ID를 이용한 검색 기능 (페이지네이션 적용)
    // - User 엔티티의 userId 필드를 기준으로 검색 (연관관계 탐색)
    Page<Post> findByUser_UserIdContaining(String userId, Pageable pageable);
}
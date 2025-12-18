package springboot_first.pr.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


// 1️⃣ 어노테이션 선언
@Entity // 해당 클래스가 엔티티임을 선언, 클래스 필드를 바탕으로 DB에 테이블 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString
@Builder // 서비스에서 엔티티 생성 시 훨씬 편함
@Slf4j // 로깅 추가
@Table(name = "Posts") // ⚠️ 실제 DB 테이블 이름인 "Posts"를 지정
@AllArgsConstructor(access = AccessLevel.PRIVATE) // private : @Builder 어노테이션이 정상적으로 작동하기 위한 보조 역할, 외부 생성 차단
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자의 접근 권한을 protected로 설정해서 외부 생성 차단, JPA는 허용하도록 설정
// ✔ JPA 규칙 준수, 엔티티 생성 ∙ 수정 규칙 강제, 나중에 유지보수할 때 버그 확률 급감
@EntityListeners(AuditingEntityListener.class)
/* 💡 Soft Delete를 위한 핵심 어노테이션 추가 */
@SQLDelete(sql = "UPDATE posts SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?") // 1️⃣ delete 호출 시 UPDATE 실행
@SQLRestriction("deleted_at IS NULL")

public class Post {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 게시글 고유 ID (Primary Key)

  @Column(nullable = false, length = 100)
  private String title; // 게시글 제목

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content; // 게시글 내용 (TEXT 타입으로 설정하여 긴 내용 저장)
  
  // *****************************************************************
  // 핵심: User 엔티티와의 연관 관계 (N:1 관계 설정)
  // Post(N) - User(1)
  // *****************************************************************
  @ManyToOne(fetch = FetchType.LAZY) // N개의 게시글은 1명의 사용자에게 속함.
  @JoinColumn(name = "user_id", nullable = false) // 외래 키(FK) 컬럼 이름은 user_id
  private User user; // 게시글 작성자 엔티티

  // 현업 Tip: @EntityListeners(AuditingEntityListener.class) 설정이 필요합니다.
  // 이는 Spring Data JPA Auditing을 활성화하여 자동으로 날짜를 관리하게 합니다.
  
  @CreatedDate
  @Column(updatable = false) // 생성일은 업데이트되지 않음
  private LocalDateTime createdAt; // 생성 일자

  @LastModifiedDate
  private LocalDateTime updatedAt; // 최종 수정 일자

  private LocalDateTime deletedAt; // 삭제 일자 

  public static Post create(String title, String content, User author) {
    log.info("User Entity create() 메서드 호출, title: {}, content: {}, author: {}", title, content, author); 

        if (author == null) {
            throw new IllegalArgumentException("게시글 작성자 정보는 필수입니다.");
        }
        
        return Post.builder()
            .title(title)
            .content(content)
            .user(author)
            .build();
    }

  /**
   * 비즈니스 로직 : 게시글 내용 수정을 위한 메서드
   * 엔티티 내에서 데이터를 변경하는 것이 객체지향적이라고 한다
   */
  public void update(String title, String content) {
      this.title = title;
      this.content = content;
  }

  /**
   * 비즈니스 로직 : 삭제 처리 (비활성화)
   * 실제로 필드만 업데이트하거나, SQLDelete 어노테이션이 처리하게 함
   */

  public void delete(){
    this.deletedAt = LocalDateTime.now();
  }

}
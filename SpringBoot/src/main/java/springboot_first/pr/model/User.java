package springboot_first.pr.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")  // MySQL 테이블 이름과 맞춤
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String email;

    // @NotBlank
    @Column(nullable = false)
    private String username;

    // @Size(min = 8)
    @Column(nullable = false)
    // @JsonIgnore
    private String password;
    // JSON 응답에 포함 안 되게 하는 것! 또는 DTO(UserResponseDTO)를 만들어서 password 필드 자체를 빼버리기.

    private String address;

    private String phoneNumber;  // Java 변수명은 camelCase
}

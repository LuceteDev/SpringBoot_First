// 비밀번호 재설정

package springboot_first.pr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResetPwRequestDTO {
    private String email;
    private String newPassword; // JSON 필드명이 다르면 @JsonProperty 필요
}
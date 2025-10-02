// 아이디 이메일 찾기

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
public class FindIdRequestDTO {
    private String username;
    private String phoneNumber; // JSON 필드명이 다르면 @JsonProperty 필요
}
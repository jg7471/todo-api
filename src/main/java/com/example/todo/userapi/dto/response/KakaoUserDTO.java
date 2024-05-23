package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class KakaoUserDTO {
    private long id;

    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Setter
    @Getter
    @ToString
    public static class KakaoAccount { //내부클래스 static!@@ : JSON 형태로 더 편함, JSON 구조 모사

        private String email;
        private Profile profile;

        @Getter @Setter @ToString
        public static class Profile {
            private String nickname; //같으면 @JsonProperty 생략 가능

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }
    public User toEntity(String accessToken) {
        return User.builder()
                .email(this.kakaoAccount.email)
                .userName(this.kakaoAccount.profile.nickname) //내부 클래스 진입
                .password("password!")
                .profileImg(this.kakaoAccount.profile.profileImageUrl)
                .accessToken(accessToken) //accessToken @@
                .build();
    }


}

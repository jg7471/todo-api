package com.example.todo.userapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // 이메일 중복 확인 요청 처리
    // GET: /api/auth/check?email=zzzz@xxx.com
    // jpa는 pk로 조회하는 메서드는 기본 제공되지만, 다른 컬럼으로 조회하는 메서드는 기본 제공되지 않습니다.
    @GetMapping("/check")
    public ResponseEntity<?> check(String email) { //@RequestParam 생략 가능
        //@RequestBody는 HTTP 요청의 본문(body)에서 데이터를 가져와서 매핑할 때 사용
        if (email.trim().isEmpty()) {
            //trim() 공백 제거 //isEmpty() 빈문자열인지 판단
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다.");
        }

        boolean resultFlag = userService.isDuplicate(email);
        log.info("중복??? - {}", resultFlag);
        return ResponseEntity.ok().body(resultFlag);
    }

    // 회원 가입 요청 처리
    // POST: /api/auth
    @PostMapping
    public ResponseEntity<?> signUp(
            @Validated @RequestPart("user") UserSignUpRequestDTO dto, //@RequestBody : json->자바 객체 변환, 이미지 : @RequestPart(value = "user") //1개는 value 생략 가능
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            BindingResult result
    ) {
        log.info("/api/auth POST! - {}", dto);

        //메서드화 ctrl alt m
        ResponseEntity<FieldError> resultEntity = getFieldErrorResponseEntity(result);
        if (resultEntity != null) return resultEntity;

        try {
            String uploadedFilePath = null;
            if (profileImage != null) {
                log.info("attached file name: {}", profileImage.getOriginalFilename());
                // 전달받은 프로필 이미지를 먼저 지정된 경로에 저장한 후 저장 경로를 DB에 세팅하자.
                uploadedFilePath = userService.uploadProfileImage(profileImage);
            }

            UserSignUpResponseDTO responseDTO = userService.create(dto, uploadedFilePath);
            return ResponseEntity.ok().body(responseDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 로그인 요청 처리 메서드를 선언하세요.
    // LoginRequestDTO 클래스를 생성해서 요청 값을 받아 주세요.
    // 서비스로 넘겨서, 로그인 유효성을 검증하세요. (비밀번호 암호화되어 있어요.)
    // 로그인 결과를 응답 상태 코드로 구분해서 보내 주세요.
    // 로그인이 성공했다면 200, 로그인 실패라면 400을 보내주세요 (에러 메세지를 상황에 따라 다르게 전달해 주세요.)
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(
            @Validated @RequestBody LoginRequestDTO dto,
            BindingResult result
    ) {
        log.info("/api/auth/signin - POST - {}", dto);

        ResponseEntity<FieldError> response = getFieldErrorResponseEntity(result);
        if (response != null) return response;

        LoginResponseDTO responseDTO = userService.authenticate(dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 일반 회원을 프리미엄 회원으로 승격하는 요청 처리
    @PutMapping("/promote")
    // 권한 검사 (해당 권한이 아니라면 인가처리 거부 -> 403 상태 리턴)
    // 메서드 호출 전에 검사 -> 요청 당시 토큰에 있는 user 정보가 ROLE_COMMON이라는 권한을 가지고 있는지를 검사.
    @PreAuthorize("hasRole('ROLE_COMMON')")
    public ResponseEntity<?> promote(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        log.info("/api/auth/promote - PUT!");

        LoginResponseDTO responseDTO = userService.promoteToPremium(userInfo);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 프로필 사진 이미지 데이터를 클라이언트에게 응답 처리
    @GetMapping("/load-profile")
    public ResponseEntity<?> loadFile(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        try {
            // 1. 프로필 사진의 경로부터 얻어야 한다.
            String filePath = userService.findProfilePath(userInfo.getUserId());
            log.info("filePath: {}", filePath);

            // 2. 얻어낸 파일 경로를 통해 실제 파일 데이터를 로드하기.
            File profileFile = new File(filePath);

            //모든 사용자가 프로필 사진을 갖는 것은 아니다
            //-> 프사를 등록하지 않은 사람은 해당 경로가 존재하지 않을 것
            //만약 존재하지 않는 경로라면 클라이언트로 404 status를 리턴
            if (!profileFile.exists()) {
                //만약 조회한 파일 경로가 http://~~로 시작한다면 -> 카카오 로그인 한 사람~!★
                if(filePath.startsWith("http://")) {
                    //카카오 로그인 프로필은 변환 과정 없이 바로 이미지 url을 리턴
                    return ResponseEntity.ok().body(filePath);
                }
                return ResponseEntity.notFound().build();
            }

            // 해당 경로에 저장된 파일을 바이트 배열로 직렬화 해서 리턴
            byte[] fileData = FileCopyUtils.copyToByteArray(profileFile);

            // 3. 응답 헤더에 컨텐츠 타입을 설정
            HttpHeaders headers = new HttpHeaders();
            MediaType contentType = findExtensionAndGetMediaType(filePath);
            if (contentType == null) {
                return ResponseEntity.internalServerError()
                        .body("발견된 파일은 이미지 파일이 아닙니다.");
            }
            headers.setContentType(contentType);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/kakaologin")
    public ResponseEntity<?> kakaoLogin(String code) { //@
        log.info("/api/auth/kakaoLogin - GET! code: {}", code);
        LoginResponseDTO responseDTO = userService.kakaoService(code);

        return ResponseEntity.ok().body(responseDTO);
    }

    //로그아웃 처리
    @GetMapping("/logout")
    //@PreAuthorize("hasRole(ROLE_SNS)")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ){
        log.info("/api/auth/logout - GET! - user: {}",userInfo.getEmail());

        String result = userService.logout(userInfo);

        return ResponseEntity.ok().body(result);
    }

    private MediaType findExtensionAndGetMediaType(String filePath) {

        // 파일 경로에서 확장자 추출
        // C:/todo_upload/nlskdnakscnlknklcs_abc.jpg
        String ext
                = filePath.substring(filePath.lastIndexOf(".") + 1);

        // 추출한 확장자를 바탕으로 MediaType을 설정 -> Header에 들어갈 Content-type이 됨.
        switch (ext.toUpperCase()) {
            case "JPG": case "JPEG":
                return MediaType.IMAGE_JPEG;
            case "PNG":
                return MediaType.IMAGE_PNG;
            case "GIF":
                return MediaType.IMAGE_GIF;
            default:
                return null;
        }
    }

    private static ResponseEntity<FieldError> getFieldErrorResponseEntity(BindingResult result) {
        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }
        return null;
    }

}

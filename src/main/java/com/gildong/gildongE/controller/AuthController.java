package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.UserResponse;
import com.gildong.gildongE.service.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<UserResponse> kakaoCallback(@RequestParam String code) {
        try {
            UserResponse user = kakaoOAuthService.kakaoLogin(code);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

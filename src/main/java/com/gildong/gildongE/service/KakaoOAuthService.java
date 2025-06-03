package com.gildong.gildongE.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gildong.gildongE.dto.UserResponse;
import com.gildong.gildongE.model.AuthProvider;
import com.gildong.gildongE.model.User;
import com.gildong.gildongE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String clientId = "dba02cfce15347e2fc158f42700ad854";
    private final String redirectUri = "http://<redacted-ip>:8080/api/auth/kakao/callback";

    public UserResponse kakaoLogin(String code) throws Exception {
        // 1. 액세스 토큰 요청
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String tokenResponse = restTemplate.postForObject("https://kauth.kakao.com/oauth/token", request, String.class);
        String accessToken = objectMapper.readTree(tokenResponse).get("access_token").asText();

        // 2. 사용자 정보 요청
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> userRequest = new HttpEntity<>(userHeaders);

        String userInfoResponse = restTemplate.postForObject("https://kapi.kakao.com/v2/user/me", userRequest, String.class);
        JsonNode userJson = objectMapper.readTree(userInfoResponse);

        // 3. 고유 Kakao ID 기반 loginId 생성
        String kakaoId = userJson.get("id").asText();
        String loginId = "kakao_" + kakaoId + "@kakao.com";

        String nickname = userJson.get("properties").get("nickname").asText();

        // 4. 기존 유저 존재 확인
        Optional<User> existingUser = userRepository.findByLoginId(loginId);
        if (existingUser.isPresent()) {
            return userService.toResponse(existingUser.get());
        }

        // 5. 신규 유저 저장
        User newUser = new User();
        newUser.setLoginId(loginId);
        newUser.setUserName(nickname);
        newUser.setPassword(null);
        newUser.setEmail(null);  // email은 null 처리 또는 모델에서 nullable 처리
        newUser.setProvider(AuthProvider.KAKAO);
        newUser.setCreatedAt(LocalDateTime.now());

        return userService.toResponse(userRepository.save(newUser));
    }
}

package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.LoginRequest;
import com.gildong.gildongE.dto.UserCreateRequest;
import com.gildong.gildongE.dto.UserUpdateRequest;
import com.gildong.gildongE.dto.UserResponse;
import com.gildong.gildongE.model.User;
import com.gildong.gildongE.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /** 회원가입 */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(req));
    }

    /** 프로필 조회 */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    /** 사용자 정보(이름/비밀번호) 수정 */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(userService.updateUser(userId, req));
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest req) {
        User u = userService.getByLoginId(req.getLoginId());
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getUserById(u.getId()));
    }

    /** loginId로 사용자 조회 */
    @GetMapping("/loginId")
    public ResponseEntity<UserResponse> getUserByLoginId(@RequestParam String loginId) {
        return ResponseEntity.ok(userService.getByLoginIdAsResponse(loginId));
    }


}

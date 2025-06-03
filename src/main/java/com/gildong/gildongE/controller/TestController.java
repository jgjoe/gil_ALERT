package com.gildong.gildongE.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "서버 정상 작동 중";
    }

    @GetMapping("/success")
    public String success() {
        return "카카오 로그인 성공!";
    }
}

package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.DrivingPatternRequest;
import com.gildong.gildongE.dto.DrivingPatternResponse;
import com.gildong.gildongE.dto.WeeklyAverageResponse;
import com.gildong.gildongE.service.DrivingPatternService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patterns")
public class DrivingPatternController {
    private final DrivingPatternService patternService;

    public DrivingPatternController(DrivingPatternService patternService) {
        this.patternService = patternService;
    }

    /** 운전 점수 기록 */
    @PostMapping
    public ResponseEntity<DrivingPatternResponse> record(
            @RequestBody DrivingPatternRequest req) {
        DrivingPatternResponse resp = patternService.recordPattern(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /** 사용자별 운전 기록 조회 */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DrivingPatternResponse>> listByUser(
            @PathVariable String userId) {
        List<DrivingPatternResponse> list = patternService.listPatterns(userId);
        return ResponseEntity.ok(list);
    }
    /** 사용자 이름으로 운전 기록 조회 */
    @GetMapping("/username/{userName}")
    public ResponseEntity<List<DrivingPatternResponse>> listByUserName(
            @PathVariable String userName) {
        List<DrivingPatternResponse> list = patternService.listPatternsByUserName(userName);
        return ResponseEntity.ok(list);
    }


    /** 사용자별 일주일 단위 평균 점수 목록 반환 */
    @GetMapping("/user/{userId}/weekly-averages")
    public ResponseEntity<List<WeeklyAverageResponse>> getWeeklyAverages(
            @PathVariable String userId) {
        List<WeeklyAverageResponse> result = patternService.getWeeklyAverages(userId);
        return ResponseEntity.ok(result);
    }
}

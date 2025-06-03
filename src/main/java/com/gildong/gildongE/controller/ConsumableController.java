package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.ConsumableRequest;
import com.gildong.gildongE.dto.ConsumableResponse;
import com.gildong.gildongE.dto.ConsumablesOverviewResponse;
import com.gildong.gildongE.service.ConsumableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumables")
public class ConsumableController {
    private final ConsumableService consumableService;

    public ConsumableController(ConsumableService consumableService) {
        this.consumableService = consumableService;
    }

    /** POST /api/consumables */
    @PostMapping
    public ResponseEntity<ConsumableResponse> saveConsumable(
            @RequestBody ConsumableRequest req) {
        ConsumableResponse resp = consumableService.saveConsumable(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /** GET /api/consumables/user/{userId} */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConsumableResponse>> getConsumables(
            @PathVariable String userId) {
        return ResponseEntity.ok(consumableService.listConsumables(userId));
    }

    /** GET /api/consumables/user/{userId}/overview */
    @GetMapping("/user/{userId}/overview")
    public ResponseEntity<ConsumablesOverviewResponse> getOverview(
            @PathVariable String userId) {
        return ResponseEntity.ok(consumableService.getConsumablesOverview(userId));
    }
}

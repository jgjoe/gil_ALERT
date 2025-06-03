package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.ConsumableRequest;
import com.gildong.gildongE.dto.ConsumableResponse;
import com.gildong.gildongE.dto.ConsumablesOverviewResponse;
import com.gildong.gildongE.exception.ResourceNotFoundException;
import com.gildong.gildongE.model.Consumable;
import com.gildong.gildongE.repository.ConsumableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConsumableService {
    private final ConsumableRepository consumableRepo;
    private final UserService userService;

    public ConsumableService(ConsumableRepository consumableRepo,
                             UserService userService) {
        this.consumableRepo = consumableRepo;
        this.userService    = userService;
    }

    /** 소모품(차량 교체 날짜) 정보 저장 */
    public ConsumableResponse saveConsumable(ConsumableRequest req) {
        // 존재 여부 검증
        userService.getUserById(req.getUserId());

        Consumable e = new Consumable();
        e.setUserId(req.getUserId());
        e.setCarModel(req.getCarModel());
        e.setCarNumber(req.getCarNumber());
        e.setEngineOilDate(req.getEngineOilDate());
        e.setBatteryDate(req.getBatteryDate());
        e.setCoolantDate(req.getCoolantDate());
        e.setTransmissionOilDate(req.getTransmissionOilDate());
        e.setBrakeOilDate(req.getBrakeOilDate());
        e.setAirconFilterDate(req.getAirconFilterDate());

        // 교체한 날짜도 저장
        e.setEngineOilChangedDate(req.getEngineOilChangedDate());
        e.setBatteryChangedDate(req.getBatteryChangedDate());
        e.setCoolantChangedDate(req.getCoolantChangedDate());
        e.setTransmissionOilChangedDate(req.getTransmissionOilChangedDate());
        e.setBrakeOilChangedDate(req.getBrakeOilChangedDate());
        e.setAirconFilterChangedDate(req.getAirconFilterChangedDate());

        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());

        Consumable saved = consumableRepo.save(e);
        return toResponse(saved);
    }


    /** 사용자별 소모품 내역 조회 */
    public List<ConsumableResponse> listConsumables(String userId) {
        // 존재 여부 검증
        userService.getUserById(userId);

        return consumableRepo
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** 전체 리스트 + 가장 가까운 교체 예정일 계산 */
    public ConsumablesOverviewResponse getConsumablesOverview(String userId) {
        List<ConsumableResponse> list = listConsumables(userId);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate today = LocalDate.now();

        // 모든 날짜 필드를 꺼내서 미래 날짜만 필터, 가장 작은 값 선택
        Optional<String> next = list.stream()
                .flatMap(c -> Stream.of(
                        c.getEngineOilDate(),
                        c.getBatteryDate(),
                        c.getCoolantDate(),
                        c.getTransmissionOilDate(),
                        c.getBrakeOilDate(),
                        c.getAirconFilterDate()
                ))
                .filter(Objects::nonNull)
                .map(d -> LocalDate.parse(d, fmt))
                .filter(d -> !d.isBefore(today))
                .min(Comparator.naturalOrder())
                .map(d -> d.format(fmt));

        ConsumablesOverviewResponse resp = new ConsumablesOverviewResponse();
        resp.setAll(list);
        resp.setNextDueDate(next.orElse(null));
        return resp;
    }

    private ConsumableResponse toResponse(Consumable e) {
        ConsumableResponse dto = new ConsumableResponse();
        dto.setId(e.getId());
        dto.setUserId(e.getUserId());
        dto.setCarModel(e.getCarModel());
        dto.setCarNumber(e.getCarNumber());
        dto.setEngineOilDate(e.getEngineOilDate());
        dto.setBatteryDate(e.getBatteryDate());
        dto.setCoolantDate(e.getCoolantDate());
        dto.setTransmissionOilDate(e.getTransmissionOilDate());
        dto.setBrakeOilDate(e.getBrakeOilDate());
        dto.setAirconFilterDate(e.getAirconFilterDate());

        // 교체한 날짜도 포함
        dto.setEngineOilChangedDate(e.getEngineOilChangedDate());
        dto.setBatteryChangedDate(e.getBatteryChangedDate());
        dto.setCoolantChangedDate(e.getCoolantChangedDate());
        dto.setTransmissionOilChangedDate(e.getTransmissionOilChangedDate());
        dto.setBrakeOilChangedDate(e.getBrakeOilChangedDate());
        dto.setAirconFilterChangedDate(e.getAirconFilterChangedDate());

        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

}

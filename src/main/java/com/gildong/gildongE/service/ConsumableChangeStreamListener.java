package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.AlertDto;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.UpdateDescription;
import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class ConsumableChangeStreamListener {

    private final AlertService alertService;
    private final MongoClient mongoClient;

    public ConsumableChangeStreamListener(AlertService alertService,
                                          MongoClient mongoClient) {
        this.alertService = alertService;
        this.mongoClient = mongoClient;
    }

    /**
     * 애플리케이션 시작 직후 Change Stream 구독
     */
    @PostConstruct
    public void init() {
        System.out.println("▶ ChangeStreamListener 초기화, 구독을 시작합니다…");
        CompletableFuture.runAsync(() -> {
            MongoDatabase database = mongoClient.getDatabase("gildongE_db");  // DB 이름
            watchConsumables(database);
        });
    }

    private void watchConsumables(MongoDatabase database) {
        MongoCollection<Document> consumablesColl = database.getCollection("consumables");
        System.out.println("Consumables 컬렉션 감시를 위해 ChangeStream 커서를 엽니다.");

        MongoCursor<ChangeStreamDocument<Document>> cursor =
                consumablesColl.watch(
                        Arrays.asList(
                                Aggregates.match(
                                        Filters.in("operationType", Arrays.asList("insert", "update"))
                                )
                        )
                )
                        .fullDocument(FullDocument.UPDATE_LOOKUP)
                        .iterator();

        System.out.println("ChangeStream 커서가 정상 생성되었습니다. 이제 이벤트를 기다립니다.");

        while (cursor.hasNext()) {
            System.out.println("ChangeStream: 이벤트 대기 중...");
            ChangeStreamDocument<Document> change = cursor.next();
            System.out.println("ChangeStream: 이벤트 감지됨 -> operationType=" + change.getOperationType());
            
            // fullDocument가 null이 아니도록 fullDocument.UPDATE_LOOKED_UP 옵션을 걸었음
            Document fullDoc = change.getFullDocument();
            if (fullDoc == null) continue;

            UpdateDescription updDesc = change.getUpdateDescription();
            if (updDesc == null) continue;

            BsonDocument updatedFields = updDesc.getUpdatedFields();
            if (updatedFields == null || updatedFields.isEmpty()) continue;

            // “교체 완료” 알림을 트리거할 수 있는 모든 필드 이름 배열
            String[] consumableFields = {
                    "engineOilChangedDate", "batteryChangedDate", "coolantChangedDate",
                    "transmissionOilChangedDate", "brakeOilChangedDate", "airconFilterChangedDate"
            };

            for (String fieldName : consumableFields) {
                if (updatedFields.containsKey(fieldName)) {
                    // 해당 필드가 업데이트된 상태라면 → 교체 완료 알림
                    // (이전 값이 null 이었든 아니었든, “날짜가 바뀐” 시점이라고 간주)
                    Object newValueObj = fullDoc.get(fieldName);
                    if (newValueObj != null) {
                        String newDateStr = newValueObj.toString();
                        AlertDto alert = new AlertDto();
                        alert.setId(UUID.randomUUID().toString());
                        alert.setUserId(fullDoc.getString("userId"));
                        alert.setType("CONSUMABLE_REPLACED");
                        alert.setTitle("차량소모품");
                        alert.setMessage(
                                "[" + fullDoc.getString("carModel") + " / " + fullDoc.getString("carNumber") + "]\n" +
                                        toKoreanFieldName(fieldName) + " 교체 완료: " + formatDateString(newDateStr)
                        );
                        alert.setCreatedAt(Instant.now());
                        alertService.pushAlert(alert);
                    }
                }
            }
        }
    }

    /**
     * 필드명을 한글 사용자 메시지로 바꿔줍니다.
     */
    private String toKoreanFieldName(String fieldName) {
        return switch (fieldName) {
            case "engineOilChangedDate" -> "엔진 오일";
            case "batteryChangedDate" -> "배터리";
            case "coolantChangedDate" -> "부동액";
            case "transmissionOilChangedDate" -> "변속기 오일";
            case "brakeOilChangedDate" -> "브레이크 오일";
            case "airconFilterChangedDate" -> "에어컨 필터";
            default -> fieldName;
        };
    }

    /**
     * YYYYMMDD 형식의 문자열을 “MM월 DD일” 형태로 바꿔줍니다.
     * 예) "20250510" → "05월 10일"
     */
    private String formatDateString(String yyyymmdd) {
        if (yyyymmdd == null || yyyymmdd.length() != 8) return yyyymmdd;
        String month = yyyymmdd.substring(4, 6);
        String day   = yyyymmdd.substring(6, 8);
        return month + "월 " + day + "일";
    }
}

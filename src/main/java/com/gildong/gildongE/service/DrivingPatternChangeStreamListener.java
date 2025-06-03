// src/main/java/com/yourcompany/gil/service/DrivingPatternChangeStreamListener.java
package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.AlertDto;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class DrivingPatternChangeStreamListener {

    private final AlertService alertService;
    private final MongoClient mongoClient;

    public DrivingPatternChangeStreamListener(AlertService alertService,
                                              MongoClient mongoClient) {
        this.alertService = alertService;
        this.mongoClient = mongoClient;
    }

    /**
     * 애플리케이션 로딩 후 자동으로 Change Stream 구독 시작
     */
    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(() -> {
            MongoDatabase database = mongoClient.getDatabase("gildongE_db");  // DB 이름이 "gil"이라고 가정
            watchDrivingPatterns(database);
        });
    }

    private void watchDrivingPatterns(MongoDatabase database) {
        MongoCollection<Document> patternsColl = database.getCollection("driving_pattern");

        MongoCursor<ChangeStreamDocument<Document>> cursor =
                patternsColl.watch(
                        Arrays.asList(
                                Aggregates.match(
                                        Filters.in("operationType", Arrays.asList("insert"))
                                )
                        )
                ).iterator();

        while (cursor.hasNext()) {
            ChangeStreamDocument<Document> change = cursor.next();
            Document fullDoc = change.getFullDocument();
            if (fullDoc == null) continue;

            // drivingScore는 Double 형태로 들어오므로 float로 변환
            float score = fullDoc.getDouble("drivingScore").floatValue();
            if (score <= 50.0f) {
                AlertDto alert = new AlertDto();
                alert.setId(UUID.randomUUID().toString());
                alert.setUserId(fullDoc.getString("userId"));
                alert.setType("DRIVING_SCORE_LOW");
                alert.setTitle("안전 알림: 운전 점수 경고");
                alert.setMessage(
                        "최근 운전 점수가 " + score + "점으로 낮게 나왔습니다. " +
                                "부드러운 운전으로 점수를 올려볼까요?"
                );
                alert.setCreatedAt(Instant.now());
                alertService.pushAlert(alert);
            }
        }
    }
}

package com.example.finpay.audit_service.health;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.ListEntitiesOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("tableStorage")
@RequiredArgsConstructor
public class TableStorageHealthIndicator implements HealthIndicator {

    private final TableClient tableClient;

    @Override
    public Health health() {
        try {
            tableClient.listEntities(new ListEntitiesOptions().setTop(1), null, null);
            return Health.up()
                    .withDetail("table", tableClient.getTableName())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("table", tableClient.getTableName())
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}

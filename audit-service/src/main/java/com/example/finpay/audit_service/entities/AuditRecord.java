package com.example.finpay.audit_service.entities;

import com.azure.data.tables.models.TableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuditRecord {

    private String partitionKey;
    private String rowKey;
    private String serviceName;
    private String action;
    private String entityType;
    private String entityId;
    private String userId;
    private String status;
    private String details;
    private String errorMessage;
    private String ipAddress;
    private OffsetDateTime timestamp;

    public static AuditRecord create(String serviceName,
                                     String action,
                                     String entityType,
                                     String entityId,
                                     String userId,
                                     String status,
                                     String details,
                                     String ipAddress) {
        OffsetDateTime now = OffsetDateTime.now();
        String rowKey = now.toInstant().toEpochMilli() + "_" + UUID.randomUUID();

        return AuditRecord.builder()
                .partitionKey(serviceName)
                .rowKey(rowKey)
                .serviceName(serviceName)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .status(status)
                .details(details)
                .ipAddress(ipAddress)
                .timestamp(now)
                .build();
    }

    public TableEntity toTableEntity() {
        TableEntity entity = new TableEntity(partitionKey, rowKey);
        entity.addProperty("serviceName", serviceName);
        entity.addProperty("action", action);
        entity.addProperty("entityType", entityType);
        entity.addProperty("entityId", entityId);
        entity.addProperty("userId", userId);
        entity.addProperty("status", status);
        entity.addProperty("details", details);
        entity.addProperty("errorMessage", errorMessage);
        entity.addProperty("ipAddress", ipAddress);
        entity.addProperty("timestamp", timestamp);
        return entity;
    }

    public static AuditRecord fromTableEntity(TableEntity entity) {
        return AuditRecord.builder()
                .partitionKey(entity.getPartitionKey())
                .rowKey(entity.getRowKey())
                .serviceName((String) entity.getProperty("serviceName"))
                .action((String) entity.getProperty("action"))
                .entityType((String) entity.getProperty("entityType"))
                .entityId((String) entity.getProperty("entityId"))
                .userId((String) entity.getProperty("userId"))
                .status((String) entity.getProperty("status"))
                .details((String) entity.getProperty("details"))
                .errorMessage((String) entity.getProperty("errorMessage"))
                .ipAddress((String) entity.getProperty("ipAddress"))
                .timestamp((OffsetDateTime) entity.getProperty("timestamp"))
                .build();
    }
}

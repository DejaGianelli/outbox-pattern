package com.drivelab.outbox.pattern.messaging;

import java.util.UUID;

public class PushNotificationMessage {
    private final UUID customerId;
    private final String dishName;

    public PushNotificationMessage(UUID customerId, String dishName) {
        this.customerId = customerId;
        this.dishName = dishName;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getDishName() {
        return dishName;
    }
}

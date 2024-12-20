package com.drivelab.outbox.pattern.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public class TicketDoneMessage {
    private final UUID externalId;
    private final String dishName;
    private final LocalDateTime doneAt;

    public TicketDoneMessage(UUID externalId, String dishName, LocalDateTime doneAt) {
        this.externalId = externalId;
        this.dishName = dishName;
        this.doneAt = doneAt;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public String getDishName() {
        return dishName;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }
}

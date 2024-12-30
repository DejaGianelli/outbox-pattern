package com.drivelab.outbox.pattern.app.api;

import com.drivelab.outbox.pattern.app.domain.entities.TicketStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class ChangeTicketStatusResponse {
    @JsonProperty("id")
    private UUID externalId;
    private TicketStatus status;
    private String dishName;
    private LocalDateTime doneAt;

    private ChangeTicketStatusResponse(Builder builder) {
        setExternalId(builder.externalId);
        setStatus(builder.status);
        setDishName(builder.dishName);
        setDoneAt(builder.doneAt);
    }

    public UUID getExternalId() {
        return externalId;
    }

    public void setExternalId(UUID externalId) {
        this.externalId = externalId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(LocalDateTime doneAt) {
        this.doneAt = doneAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID externalId;
        private TicketStatus status;
        private String dishName;
        private LocalDateTime doneAt;

        private Builder() {
        }

        public Builder externalId(UUID val) {
            externalId = val;
            return this;
        }

        public Builder status(TicketStatus val) {
            status = val;
            return this;
        }

        public Builder dishName(String val) {
            dishName = val;
            return this;
        }

        public Builder doneAt(LocalDateTime val) {
            doneAt = val;
            return this;
        }

        public ChangeTicketStatusResponse build() {
            return new ChangeTicketStatusResponse(this);
        }
    }
}

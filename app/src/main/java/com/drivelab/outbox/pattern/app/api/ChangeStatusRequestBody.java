package com.drivelab.outbox.pattern.app.api;

import com.drivelab.outbox.pattern.app.domain.entities.TicketStatus;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ChangeStatusRequestBody {
    private TicketStatus status;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}

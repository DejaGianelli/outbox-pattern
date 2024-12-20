package com.drivelab.outbox.pattern.api;

import com.drivelab.outbox.pattern.domain.entities.Ticket;
import com.drivelab.outbox.pattern.domain.services.ChangeTicketStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/tickets")
public class TicketController {

    private final ChangeTicketStatusService changeTicketStatusService;

    @Autowired
    public TicketController(ChangeTicketStatusService changeTicketStatusService) {
        this.changeTicketStatusService = changeTicketStatusService;
    }

    @PostMapping("/{ticketId}/status")
    public ResponseEntity<ChangeTicketStatusResponse> changeStatus(@PathVariable UUID ticketId,
                                                                   @RequestBody ChangeStatusRequestBody requestBody) {
        Ticket ticket = changeTicketStatusService.handle(ticketId, requestBody.getStatus());

        ChangeTicketStatusResponse response = getResponse(ticket);

        return ResponseEntity.ok(response);
    }

    private ChangeTicketStatusResponse getResponse(Ticket ticket) {
        return ChangeTicketStatusResponse.builder()
                .status(ticket.getStatus())
                .doneAt(ticket.getDoneAt())
                .dishName(ticket.getDishName())
                .externalId(ticket.getExternalId())
                .build();
    }
}

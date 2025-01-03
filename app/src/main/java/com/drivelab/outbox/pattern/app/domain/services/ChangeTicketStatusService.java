package com.drivelab.outbox.pattern.app.domain.services;

import com.drivelab.outbox.pattern.app.domain.entities.Ticket;
import com.drivelab.outbox.pattern.app.domain.entities.TicketRepository;
import com.drivelab.outbox.pattern.app.domain.entities.TicketStatus;
import com.drivelab.outbox.pattern.app.messaging.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeTicketStatusService {

    private final TicketRepository ticketRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChangeTicketStatusService(TicketRepository ticketRepository,
                                     OutboxRepository outboxRepository,
                                     ObjectMapper objectMapper) {
        this.ticketRepository = ticketRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Ticket handle(UUID externalId, TicketStatus status) {

        //Retrieve Ticket from database
        Ticket ticket = ticketRepository.getByExternalId(externalId);

        //Update status of the ticket and persist
        ticket.update(status);
        ticket = ticketRepository.save(ticket);

        //Persist events in the Outbox
        outboxRepository.save(new Outbox(getTicketDoneMessagePayload(ticket), Channel.TICKET_EVENT));
        outboxRepository.save(new Outbox(getPushNotificationMessagePayload(ticket), Channel.PUSH_NOTIFICATION));
        //Uncomment the line below to simulate resilience to failing
        //outboxRepository.save(new Outbox(getNonExistentMessagePayload(ticket), Channel.NON_EXISTENT));

        return ticket;
    }

    private String getNonExistentMessagePayload(Ticket ticket) {
        try {
            return objectMapper.writeValueAsString(new NonExistentMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPushNotificationMessagePayload(Ticket ticket) {
        try {
            PushNotificationMessage message = new PushNotificationMessage(ticket.getCustomerId(),
                    ticket.getDishName());
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTicketDoneMessagePayload(Ticket ticket) {
        try {
            TicketDoneMessage message = new TicketDoneMessage(ticket.getExternalId(),
                    ticket.getDishName(),
                    ticket.getDoneAt());
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

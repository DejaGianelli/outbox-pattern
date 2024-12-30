package com.drivelab.outbox.pattern.app.domain.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Ticket getByExternalId(UUID externalId);
}

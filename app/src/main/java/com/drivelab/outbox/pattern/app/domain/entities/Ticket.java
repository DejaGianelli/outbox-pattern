package com.drivelab.outbox.pattern.app.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar")
    private UUID externalId;

    @Column(columnDefinition = "varchar")
    private UUID customerId;

    private String dishName;

    @Column(columnDefinition = "ticket_status")
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime doneAt;

    private LocalDateTime createdAt;

    public Ticket(String dishName) {
        this();
        this.dishName = dishName;
    }

    public Ticket() {
        this.status = TicketStatus.PREPARING;
        this.externalId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    public void update(TicketStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
        if (status == TicketStatus.DONE) {
            this.setDoneAt(LocalDateTime.now());
        }
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public void setExternalId(UUID externalId) {
        this.externalId = externalId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(LocalDateTime doneAt) {
        this.doneAt = doneAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public Ticket clone() {
        try {
            return (Ticket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

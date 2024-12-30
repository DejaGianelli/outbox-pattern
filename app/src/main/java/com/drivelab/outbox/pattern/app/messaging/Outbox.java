package com.drivelab.outbox.pattern.app.messaging;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "outbox")
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private String payload;

    @Column(name = "occurred_at_utc")
    private OffsetDateTime occurredAt;

    private Outbox() {
        //Empty constructor for JPA
    }

    public Outbox(String payload, Channel channel) {
        this.payload = payload;
        this.channel = channel;
        this.occurredAt = OffsetDateTime.now(ZoneId.of("UTC"));
    }

    public Channel getChannel() {
        return channel;
    }

    public Integer getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}

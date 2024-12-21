package com.drivelab.outbox.pattern.messaging;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

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

    private LocalDateTime createdAt;

    private Outbox() {
        //Empty constructor for JPA
    }

    public Outbox(String payload, Channel channel) {
        this.payload = payload;
        this.channel = channel;
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

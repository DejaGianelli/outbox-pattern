package com.drivelab.outbox.pattern.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Integer> {
    @Query("""
            SELECT to FROM Outbox to
            WHERE to.channel = :channel
            ORDER BY to.id ASC
            LIMIT :chunkSize
            """)
    List<Outbox> findAllMessagesNotSent(Channel channel, int chunkSize);
}

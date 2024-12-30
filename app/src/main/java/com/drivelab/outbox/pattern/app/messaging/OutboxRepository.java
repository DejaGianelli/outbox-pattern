package com.drivelab.outbox.pattern.app.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Integer> {
    @Query("""
            SELECT ob FROM Outbox ob
            WHERE ob.channel = :channel
            ORDER BY ob.id ASC
            LIMIT :chunkSize
            """)
    List<Outbox> findAllMessagesNotSent(Channel channel, int chunkSize);
}

package com.drivelab.outbox.pattern.scheduling;

import com.drivelab.outbox.pattern.messaging.Channel;
import com.drivelab.outbox.pattern.messaging.Outbox;
import com.drivelab.outbox.pattern.messaging.OutboxRepository;
import io.awspring.cloud.sqs.operations.SendResult.Batch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.lang.String.valueOf;

public abstract class BaseOutboxPoller {
    private static final Logger logger = LoggerFactory.getLogger(BaseOutboxPoller.class);
    private static final String HEADER_OUTBOX_ID = "outbox_id";

    private final OutboxRepository outboxRepository;

    @Autowired
    public BaseOutboxPoller(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    protected List<Message<String>> buildMessageEntries(List<Outbox> outboxChunk) {
        List<Message<String>> messageEntries = new ArrayList<>();
        outboxChunk.forEach(outbox -> messageEntries.add(
                MessageBuilder.withPayload(outbox.getPayload())
                        .setHeader(HEADER_OUTBOX_ID, valueOf(outbox.getId()))
                        .build()
        ));
        return messageEntries;
    }

    protected BiConsumer<Batch<String>, Throwable> getOutboxSendResultHandler(final Channel channel,
                                                                              final List<Outbox> outboxChunk) {
        return (ticketOutboxBatch, throwable) -> {
            if (throwable != null) {
                logger.error("{}", throwable.getMessage(), throwable);
                return;
            }

            //Retrieve all ids of outbox chunk that were sent successfully
            List<String> successfulIds = ticketOutboxBatch.successful()
                    .stream()
                    .map(result -> (String) result.message().getHeaders().get(HEADER_OUTBOX_ID))
                    .toList();

            //Get all successful outbox entries and delete
            List<Outbox> chunkToDelete = outboxChunk.stream()
                    .filter(outbox -> successfulIds.contains(String.valueOf(outbox.getId())))
                    .toList();

            if (!chunkToDelete.isEmpty()) {
                outboxRepository.deleteAllInBatch(chunkToDelete);
                logger.info("{} messages sent successfully to channel {}", chunkToDelete.size(), channel);
            }

            // Log all failed outbox entries
            if (!ticketOutboxBatch.failed().isEmpty()) {
                logger.warn("{} messages were not sent to channel {}", ticketOutboxBatch.failed().size(), channel);
            }
        };
    }

    public abstract void poll();
}

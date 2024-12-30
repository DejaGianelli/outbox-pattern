package com.drivelab.outbox.pattern.app.scheduling;

import com.drivelab.outbox.pattern.app.messaging.Outbox;
import com.drivelab.outbox.pattern.app.messaging.OutboxRepository;
import io.awspring.cloud.sqs.operations.SendResult.Batch;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
    private static final int SQS_BATCH_LIMIT = 10;

    private final OutboxRepository outboxRepository;
    private final SqsTemplate sqsTemplate;

    @Autowired
    public BaseOutboxPoller(OutboxRepository outboxRepository, SqsTemplate sqsTemplate) {
        this.outboxRepository = outboxRepository;
        this.sqsTemplate = sqsTemplate;
    }

    protected void publishChunk(String sqsQueueName, List<Outbox> outboxChunk) {
        for (int i = 0; i < outboxChunk.size(); i++) {
            List<Outbox> batch = new ArrayList<>();
            while (batch.size() < SQS_BATCH_LIMIT && i < outboxChunk.size()) {
                batch.add(outboxChunk.get(i));
                i++;
            }
            List<Message<String>> messageEntries = buildMessageEntries(batch);
            sqsTemplate.sendManyAsync(sqsQueueName, messageEntries)
                    .whenCompleteAsync(getOutboxSendResultHandler(new ArrayList<>(batch)));
            batch.clear();
        }
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

    protected BiConsumer<Batch<String>, Throwable> getOutboxSendResultHandler(final List<Outbox> outboxChunk) {
        return (ticketOutboxBatch, throwable) -> {
            if (throwable != null) {
                Throwable rootCause = ExceptionUtils.getRootCause(throwable);
                logger.error("{}", rootCause.getMessage());
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
                logger.info("{} messages sent successfully", chunkToDelete.size());
            }

            // Log all failed outbox entries
            if (!ticketOutboxBatch.failed().isEmpty()) {
                logger.warn("{} messages were not sent", ticketOutboxBatch.failed().size());
            }
        };
    }

    public abstract void poll();
}

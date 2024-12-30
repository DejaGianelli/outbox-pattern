package com.drivelab.outbox.pattern.app.scheduling;

import com.drivelab.outbox.pattern.app.messaging.Channel;
import com.drivelab.outbox.pattern.app.messaging.Outbox;
import com.drivelab.outbox.pattern.app.messaging.OutboxRepository;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketDoneOutboxPoller extends BaseOutboxPoller {
    private static final Logger logger = LoggerFactory.getLogger(TicketDoneOutboxPoller.class);

    private final String sqsQueueName;
    private final int chunkSize;
    private final OutboxRepository outboxRepository;

    @Autowired
    public TicketDoneOutboxPoller(@Value("${messaging.sqs.queues.ticket-event}") String sqsQueueName,
                                  @Value("${messaging.sqs.polling.chunk-size}") int chunkSize,
                                  OutboxRepository outboxRepository,
                                  SqsTemplate sqsTemplate) {
        super(outboxRepository, sqsTemplate);
        this.sqsQueueName = sqsQueueName;
        this.chunkSize = chunkSize;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Scheduled(fixedDelayString = "${messaging.sqs.polling.interval-ms}")
    public void poll() {
        logger.info("Polling Outbox for TICKET_EVENT messages");
        List<Outbox> outboxChunk = outboxRepository.findAllMessagesNotSent(Channel.TICKET_EVENT, chunkSize);
        if (outboxChunk.isEmpty()) {
            return;
        }
        this.publishChunk(sqsQueueName, outboxChunk);
    }
}
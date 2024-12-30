package com.drivelab.outbox.pattern.app.scheduling;

import com.drivelab.outbox.pattern.app.messaging.Channel;
import com.drivelab.outbox.pattern.app.messaging.Outbox;
import com.drivelab.outbox.pattern.app.messaging.OutboxRepository;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NonExistentOutboxPoller extends BaseOutboxPoller {
    private static final Logger logger = LoggerFactory.getLogger(NonExistentOutboxPoller.class);

    private final String sqsQueueName;
    private final int chunkSize;
    private final OutboxRepository outboxRepository;

    @Autowired
    public NonExistentOutboxPoller(@Value("${messaging.sqs.queues.non-existent-queue}") String sqsQueueName,
                                   @Value("${messaging.sqs.polling.chunk-size}") int chunkSize,
                                   OutboxRepository outboxRepository,
                                   SqsTemplate sqsTemplate) {
        super(outboxRepository, sqsTemplate);
        this.sqsQueueName = sqsQueueName;
        this.chunkSize = chunkSize;
        this.outboxRepository = outboxRepository;
    }

    @Override
    //Uncomment the line below to simulate a failing poller
    //@Scheduled(fixedDelayString = "${messaging.sqs.polling.interval-ms}")
    public void poll() {
        logger.info("Polling Outbox for NON_EXISTENT messages");

        List<Outbox> outboxChunk = outboxRepository.findAllMessagesNotSent(Channel.NON_EXISTENT, chunkSize);
        if (outboxChunk.isEmpty()) {
            return;
        }
        this.publishChunk(sqsQueueName, outboxChunk);
    }
}
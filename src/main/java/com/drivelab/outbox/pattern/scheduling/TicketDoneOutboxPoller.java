package com.drivelab.outbox.pattern.scheduling;

import com.drivelab.outbox.pattern.messaging.Channel;
import com.drivelab.outbox.pattern.messaging.TicketOutbox;
import com.drivelab.outbox.pattern.messaging.TicketOutboxRepository;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TicketDoneOutboxPoller extends BaseOutboxPoller {
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketDoneOutboxPoller.class);

    private final String sqsQueueName;
    private final int chunkSize;
    private final TicketOutboxRepository ticketOutboxRepository;
    private final SqsTemplate sqsTemplate;

    @Autowired
    public TicketDoneOutboxPoller(@Value("${messaging.sqs.queues.ticket-event}") String sqsQueueName,
                                  @Value("${messaging.sqs.polling.chunk-size}") int chunkSize,
                                  TicketOutboxRepository ticketOutboxRepository,
                                  SqsTemplate sqsTemplate) {
        super(ticketOutboxRepository);
        this.sqsQueueName = sqsQueueName;
        this.chunkSize = chunkSize;
        this.ticketOutboxRepository = ticketOutboxRepository;
        this.sqsTemplate = sqsTemplate;
    }

    @Scheduled(fixedDelayString = "${messaging.sqs.polling.interval-ms}")
    public void poll() {
        LOGGER.info("Polling for TicketDone messages");

        List<TicketOutbox> entities = ticketOutboxRepository.findAllMessagesNotSent(Channel.TICKET_EVENT, chunkSize);
        if (entities.isEmpty()) {
            return;
        }

        List<Message<TicketOutbox>> messageEntries = new ArrayList<>();

        entities.forEach(entity -> messageEntries.add(
                MessageBuilder.withPayload(entity)
                        .setHeader("messageDeduplicationId", entity.getId())
                        .build()
        ));

        sqsTemplate.sendManyAsync(sqsQueueName, messageEntries)
                .whenCompleteAsync(getOutboxSendResultHandler(Channel.TICKET_EVENT));
    }
}
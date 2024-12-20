package com.drivelab.outbox.pattern.scheduling;

import com.drivelab.outbox.pattern.messaging.Channel;
import com.drivelab.outbox.pattern.messaging.TicketOutbox;
import com.drivelab.outbox.pattern.messaging.TicketOutboxRepository;
import io.awspring.cloud.sqs.operations.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class BaseOutboxPoller {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOutboxPoller.class);

    @Autowired
    private final TicketOutboxRepository ticketOutboxRepository;

    public BaseOutboxPoller(TicketOutboxRepository ticketOutboxRepository) {
        this.ticketOutboxRepository = ticketOutboxRepository;
    }

    protected BiConsumer<SendResult.Batch<TicketOutbox>, Throwable> getOutboxSendResultHandler(final Channel channel) {
        return (ticketOutboxBatch, throwable) -> {
            if (throwable != null) {
                LOGGER.error("{}", throwable.getMessage(), throwable);
                return;
            }

            List<TicketOutbox> successful = ticketOutboxBatch.successful()
                    .stream()
                    .map(result -> result.message().getPayload())
                    .toList();
            if (!successful.isEmpty()) {
                ticketOutboxRepository.deleteAllInBatch(successful);
                LOGGER.info("{} messages sent successfully to channel {}", successful.size(), channel);
            }

            List<TicketOutbox> failed = ticketOutboxBatch.failed()
                    .stream()
                    .map(result -> result.message().getPayload())
                    .toList();
            if (!failed.isEmpty()) {
                LOGGER.warn("{} messages were not sent to channel {}", successful.size(), channel);
            }
        };
    }
}

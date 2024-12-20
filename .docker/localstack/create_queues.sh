#!/bin/bash
awslocal sqs create-queue --queue-name TicketEvent.fifo --attributes '{"FifoQueue": "true", "ContentBasedDeduplication": "false"}'
awslocal sqs create-queue --queue-name PushNotification.fifo --attributes '{"FifoQueue": "true", "ContentBasedDeduplication": "false"}'
awslocal sqs list-queues
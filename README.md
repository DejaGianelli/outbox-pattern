# Outbox Pattern (Transactional Messages)

Receive messages from queue:

```shell
awslocal sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/TicketEvent.fifo
```

```shell
awslocal sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/PushNotification.fifo
```
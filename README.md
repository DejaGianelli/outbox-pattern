# Outbox Pattern (Transactional Messages)

In distributed architectures, it is common for a service to perform database operations and publish events to a 
messaging system. These operations may involve publishing a single message or multiple messages. The challenge is 
ensuring that these operations (database and messaging) are atomic. Without atomicity, errors in any part of the 
process could leave the system in an inconsistent state. For example, the service might fail after committing a 
database operation but before sending the message to the broker, or it might send the message but fail to update 
the database. In either case, severe inconsistencies could occur.

The solution to this problem is to ensure the entire operation is handled within a database transaction, which is the 
purpose of the Outbox Pattern.

The concept is straightforward: instead of publishing messages directly to message queues, the messages are first 
stored in an outbox table. This leverages the transactional guarantees of relational databases and, to some extent, 
non-relational databases (with limitations). Special care is required when working with non-relational databases, 
as most of them provide transactional capabilities only at the document level.

Once the message is safely stored in the outbox table, a worker process is responsible for polling this table and 
attempting to publish the message. If the worker successfully publishes the message, it can then delete it from the 
outbox table. An alternative approach is to use a tool that tails the database commit log to listen for changes in 
the outbox table and publish messages automatically. However, for simplicity, this project implements the polling 
approach.


## Example Scenario Implemented

This project serves as a proof of concept by emulating a Restaurant Application. Imagine a scenario where a service 
responsible for the Kitchen has a REST endpoint to update the status of a Ticket. When this endpoint is called with 
the status DONE, indicating that the dish ordered by the customer is ready, it must perform two actions: update the 
Ticket status in the database and publish messages to external systems.

One message is a "done event," which might be consumed by the Order Service to update the corresponding Order's status. 
The other message is intended for a Push Notification service to send a notification to the customer’s phone.

These operations must be executed atomically and reliably, which is why the outbox pattern is implemented.


## Running the Project

To get the infrastructure up and running using docker (localstack and postgres), you can simply run:

```shell
docker compose up -d
```

To run the Service:

```shell
mvn spring-boot:run -f pom.xml
```

To call the REST endpoint to simulate an Order being marked as **DONE**:

```shell
curl --location 'http://localhost:8080/v1/tickets/48f41419-bc67-4802-a085-ad0a49b13dc1/status' \
--header 'Content-Type: application/json' \
--data '{
    "status": "DONE"
}'
```

To inspect messages in the queues:

```shell
awslocal sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/TicketEvent.fifo
```

```shell
awslocal sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/PushNotification.fifo
```
-- Create a Covering Index to speed up outbox chunk fetch
CREATE INDEX IF NOT EXISTS idx_outbox_messages_unprocessed ON
outbox (id) include (channel, payload, occurred_at_utc);
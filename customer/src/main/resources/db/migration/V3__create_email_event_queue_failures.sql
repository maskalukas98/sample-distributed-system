BEGIN;
CREATE TYPE source_enum AS ENUM ('Welcome');

CREATE TABLE email_event_queue_failures (
     id serial PRIMARY KEY,
     customer_id INT NOT NULL,
     source source_enum NOT NULL,
     FOREIGN KEY (customer_id) REFERENCES customers(id),
     CONSTRAINT unique_source_customer UNIQUE (customer_id, source)
);

COMMIT;
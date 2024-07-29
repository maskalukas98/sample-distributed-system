CREATE table customer_preferences (
      id SERIAL PRIMARY KEY,
      customer_id INT NOT NULL,
      notification_email BOOLEAN NOT NULL,
      notification_sms BOOLEAN NOT NULL,
      language VARCHAR(10) NOT NULL,
      FOREIGN KEY (customer_id) REFERENCES customers(id)
)
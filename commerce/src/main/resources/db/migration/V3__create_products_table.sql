DO $$
BEGIN
    CREATE TABLE currencies (
          currency_code varchar(3) PRIMARY KEY,
          currency_name varchar(255) NOT NULL,
          symbol varchar(5) NOT NULL
    );

    INSERT INTO currencies (currency_code, currency_name, symbol)
    VALUES ('EUR','Euro','€');

    CREATE SEQUENCE public.products_id_seq;
    CREATE TABLE products (
          id INT PRIMARY KEY NOT NULL DEFAULT public.nextShardIdSeq('products_id_seq'),
          product_name varchar(50) NOT NULL CHECK (LENGTH(product_name) >= 1),
          price numeric(10,2) NOT NULL CHECK (price >= 0),
          is_active bool NOT NULL DEFAULT true,
          partner_id INT NOT NULL,
          currency_code varchar(3) NOT NULL,
          FOREIGN KEY (partner_id) REFERENCES partners(id),
          FOREIGN KEY (currency_code) REFERENCES currencies(currency_code)
    );
END $$;
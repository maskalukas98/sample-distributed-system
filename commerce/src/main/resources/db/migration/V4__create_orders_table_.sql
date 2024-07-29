-- GENERATES NEXT ORDER ID the first 41 bits
-- the first 41 bits = milliseconds since epoch
-- the next 13 bits  = shard ids
-- the remaining 10 bits = sequence ids
CREATE OR REPLACE FUNCTION public.nextOrderId(in sequenceName varchar, OUT result bigint) AS $$
DECLARE
    -- 10 bits
    seq_id bigint;
    -- 13 bits
    shard_id int := public.getShardIndex();
    -- 41 bits
    milliseconds_now bigint;
    -- January 1, 1970
    start_epoch bigint := 1698105600011;
BEGIN
    -- ensures that the sequence id fits into 10 bits
    -- n % 1024 <= 10 bits
    SELECT nextval('public.' || sequenceName) % 1024 INTO seq_id;
    SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO milliseconds_now;
    result := (milliseconds_now - start_epoch) << 23;
    result := result | (shard_id << 10);
    result := result | seq_id;
END
$$ LANGUAGE plpgsql;


-- CREATE ORDERS TABLE
CREATE OR REPLACE FUNCTION public.createOrderTable(in month int, in year int) RETURNS VOID AS $$
DECLARE
    table_suffix varchar(14) := CAST(year AS varchar) || '_' || CAST(month AS varchar);
    statuses_table_name varchar := 'statuses_' || table_suffix;
    statuses_seq_name varchar := 'statuses_' || table_suffix || '_id_seq';
    statuses_id_generator_expr varchar := format('nextOrderId(''%I'')', statuses_seq_name);

    orders_table_name varchar := 'orders_' || table_suffix;
    orders_seq_name varchar := 'orders_' || table_suffix || '_id_seq';
    orders_id_generator_expr varchar := format('nextOrderId(''%I'')', orders_seq_name);
BEGIN
    -- CREATE ORDERS: SEQUENCE AND TABLE
    EXECUTE 'CREATE SEQUENCE public.' || orders_seq_name;
    EXECUTE FORMAT('CREATE TABLE %I (
        id BIGINT PRIMARY KEY DEFAULT %s,
        customer_id INT NOT NULL,
        product_id INT NOT NULL,
        address VARCHAR NOT NULL CHECK (LENGTH(address) >= 5),
        created_at timestamp default current_timestamp
    );', orders_table_name, orders_id_generator_expr);

    -- CREATE STATUSES: SEQUENCE AND TABLE
    -- TODO: ADD status enum
    EXECUTE 'CREATE SEQUENCE public.' || statuses_seq_name;
    EXECUTE FORMAT('CREATE TABLE %I (
            id BIGINT PRIMARY KEY DEFAULT %s,
            status varchar(20) NOT NULL,
            is_active boolean NOT NULL DEFAULT true,
            order_id BIGINT NOT NULL,
            updated_at timestamp default current_timestamp,
            FOREIGN KEY (order_id) REFERENCES %I(id),
            UNIQUE (status, order_id)
    );', statuses_table_name, statuses_id_generator_expr, orders_table_name);
END
$$ LANGUAGE plpgsql;

-- CREATES ORDERS AND STATUSES TABLE (CURRENT AND NEXT MONTH)
DO $$
DECLARE
    current_month int := EXTRACT(MONTH FROM CURRENT_DATE);
    current_year int := EXTRACT(YEAR FROM CURRENT_DATE);
    next_month int := (current_month % 12) + 1;
    next_year int;
BEGIN
    IF next_month = 1 THEN
        next_year := current_year + 1;
    ELSE
        next_year := current_year;
    end if;

    PERFORM public.createOrderTable(current_month, current_year);
    PERFORM public.createOrderTable(next_month, next_year);
END $$;
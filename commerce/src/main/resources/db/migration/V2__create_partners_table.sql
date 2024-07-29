DO $$
    BEGIN
    DECLARE
        shard_key varchar(2);
    BEGIN
        -- GENERATES PARTNERS SEQUENCE NUMBER
        CREATE SEQUENCE public.partners_id_seq;

        SELECT public.getShardKey() INTO shard_key;
        EXECUTE 'CREATE TYPE country_enum AS ENUM (''' || shard_key || ''')';

        CREATE TABLE partners (
              id INT PRIMARY KEY NOT NULL DEFAULT public.nextShardIdSeq('partners_id_seq'),
              company_name VARCHAR(50) NOT NULL UNIQUE,
              country country_enum NOT NULL,
              CONSTRAINT company_min_length_check CHECK (LENGTH(company_name) >= 1)
        );
    END;
END $$;
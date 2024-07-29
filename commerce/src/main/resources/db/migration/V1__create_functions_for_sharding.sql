-- RETURNS SHARD KEY ACCORDING TO DB (de, fr)
CREATE OR REPLACE FUNCTION public.getShardKey(OUT result varchar(2)) AS $$
BEGIN
    SELECT substring(current_database() from 'order_(.*)_db') INTO result;
END
$$ LANGUAGE plpgsql;

-- RETURNS SHARD KEY ACCORDING TO DB (de, fr)
CREATE OR REPLACE FUNCTION public.getShardIndex(OUT result smallint) AS $$
BEGIN
    CASE public.getShardKey()
        WHEN 'de' THEN result := 1;
        WHEN 'fr' THEN result := 2;
        ELSE result := NULL;
        END CASE;
END
$$ LANGUAGE plpgsql;

-- GENERATES NEW PARTNER ID: 8 bits for shard ids, 24 bits for sequence id
CREATE OR REPLACE FUNCTION public.nextShardIdSeq(in sequenceName varchar, OUT result int) AS $$
DECLARE
    seq_id bigint;
    shard_id int := public.getShardIndex();
BEGIN
    SELECT nextval('public.' || sequenceName) INTO seq_id;
    result := shard_id << 24;
    result := result | (seq_id);
END
$$ LANGUAGE plpgsql;
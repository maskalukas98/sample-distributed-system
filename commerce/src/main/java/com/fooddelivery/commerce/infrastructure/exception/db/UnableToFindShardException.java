package com.fooddelivery.commerce.infrastructure.exception.db;

public class UnableToFindShardException extends RuntimeException {
    public UnableToFindShardException(String shardKey) {
        super("Unable to find shard with key: " + shardKey + " .");
    }

    public UnableToFindShardException(Integer shardKey) {
        super("Unable to find shard with key: " + shardKey + " .");
    }
}

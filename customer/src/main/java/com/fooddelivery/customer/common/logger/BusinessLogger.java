package com.fooddelivery.customer.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BusinessLogger {
    private final Logger logger = LoggerFactory.getLogger(BusinessLogger.class);

    public void logInfo(String key, String message) {
        logger.info("business_log-" + key + ": " + message);
    }
}

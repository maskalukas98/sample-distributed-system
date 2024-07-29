package com.fooddelivery.customer.domain.port.output;

import com.fooddelivery.customer.application.event.RegistrationSuccessEvent;
import com.fooddelivery.customer.infrastructure.exception.http.ConnectionFailedException;

public interface NotificationQueuePort {
    void pushRegistrationEmailToQueue(RegistrationSuccessEvent event) throws ConnectionFailedException;
}

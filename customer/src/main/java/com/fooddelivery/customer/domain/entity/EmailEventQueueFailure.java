package com.fooddelivery.customer.domain.entity;

import com.fooddelivery.customer.domain.constant.EmailSource;
import com.fooddelivery.customer.domain.models.Entity;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class EmailEventQueueFailure extends Entity<Integer> {
    @Setter
    private CustomerId customerId;

    @Setter
    private EmailSource emailSource;

    public static EmailEventQueueFailure create(CustomerId customerId, EmailSource emailSource) {
        EmailEventQueueFailure emailEventQueueFailure = new EmailEventQueueFailure();
        emailEventQueueFailure.setCustomerId(customerId);
        emailEventQueueFailure.setEmailSource(emailSource);

        return emailEventQueueFailure;
    }
}

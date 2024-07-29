package com.fooddelivery.customer.domain.entity;

import com.fooddelivery.customer.domain.models.Entity;
import com.fooddelivery.customer.domain.valueobject.CustomerPreferenceId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class CustomerPreference extends Entity<CustomerPreferenceId> {
    private boolean notificationEmail;

    private boolean notificationSms;

    private String language;

    public static CustomerPreference createNew() {
        CustomerPreference initialPreferences = new CustomerPreference();
        initialPreferences.setNotificationEmail(true);
        initialPreferences.setNotificationSms(true);
        initialPreferences.setLanguage("CZ");

        return initialPreferences;
    }
}

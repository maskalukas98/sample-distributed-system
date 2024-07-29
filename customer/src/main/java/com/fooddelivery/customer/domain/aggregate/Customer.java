package com.fooddelivery.customer.domain.aggregate;


import com.fooddelivery.customer.domain.entity.CustomerPreference;
import com.fooddelivery.customer.domain.exception.HatefulNameViolationException;
import com.fooddelivery.customer.domain.models.AggregateRoot;
import com.fooddelivery.customer.domain.rule.hatefulname.HatefulNameRule;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import lombok.Getter;
import lombok.Setter;

public final class Customer extends AggregateRoot<CustomerId> {
    @Getter
    private String name;

    @Getter
    private String surname;

    @Getter
    @Setter

    private String email;

    @Getter
    private CustomerPreference preferences;

    public static Customer createNew(
            String name,
            String surname,
            String email
    ) {
        Customer newCustomer = new Customer();
        newCustomer.setName(name);
        newCustomer.setSurname(surname);
        newCustomer.email = email;
        newCustomer.preferences = CustomerPreference.createNew();

        return newCustomer;
    }

    public static Customer create(
            String name,
            String surname,
            String email
    ) {
        Customer newCustomer = new Customer();
        newCustomer.setName(name);
        newCustomer.setSurname(surname);
        newCustomer.email = email;

        return newCustomer;
    }

    public String getFullName() {
        return name + ' ' + surname;
    }

    public void setName(String name) {
        if(!HatefulNameRule.isValid(name)) {
            throw new HatefulNameViolationException(name);
        }

        this.name = name;
    }

    public void setSurname(String surname) {
        if(!HatefulNameRule.isValid(surname)) {
            throw new HatefulNameViolationException(surname);
        }

        this.surname = surname;
    }
}

package com.fooddelivery.customer.helper.database;

import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.constant.EmailSource;
import com.fooddelivery.customer.domain.entity.CustomerPreference;
import com.fooddelivery.customer.domain.entity.EmailEventQueueFailure;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import com.fooddelivery.customer.helper.database.type.RowId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerRepositoryTestHelper {
    private static final String customersTableName = "customers";
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert simpleJdbcInsert;

    public CustomerRepositoryTestHelper(@Qualifier("masterJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(customersTableName)
                .usingGeneratedKeyColumns("id");
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers";
        return jdbcTemplate.queryForObject(sql, customerRowMapper);
    }

    public void clearAllTables() {
        jdbcTemplate.update(
                "TRUNCATE TABLE customer_preferences, customers, email_event_queue_failures " +
                    "RESTART IDENTITY");
    }

    /**
     * @return generated customer id
     */
    public RowId insertCustomer(String name, String surname, String email) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("surname", surname);
        parameters.put("email", email);

        Number generatedId = simpleJdbcInsert.executeAndReturnKey(parameters);

        return new RowId(generatedId.intValue());
    }

    public List<Customer> getAllCustomers() {
        String sql = "SELECT * FROM customers";
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    public List<EmailEventQueueFailure> getAllEmailFailEvents() {
        String sql = "SELECT * FROM email_event_queue_failures";
        return jdbcTemplate.query(sql, emailEventQueueFailureRowMapper);
    }

    public List<CustomerPreference> getAllCustomerPreferences() {
        String sql = "SELECT * FROM customer_preferences";
        return jdbcTemplate.query(sql, customerPreferencesRowMapper);
    }

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setId(new CustomerId(rs.getInt("id")));
        customer.setName(rs.getString("name"));
        customer.setSurname(rs.getString("surname"));
        customer.setEmail(rs.getString("email"));
        return customer;
    };

    private final RowMapper<CustomerPreference> customerPreferencesRowMapper = (rs, rowNum) -> {
        CustomerPreference preference = new CustomerPreference();
        preference.setNotificationEmail(rs.getBoolean("notification_email"));
        preference.setNotificationSms(rs.getBoolean("notification_sms"));;
        preference.setLanguage(rs.getString("language"));
        return preference;
    };

    private final RowMapper<EmailEventQueueFailure> emailEventQueueFailureRowMapper = (rs, rowNum) -> {
        EmailEventQueueFailure emailEventQueueFailure = new EmailEventQueueFailure();
        emailEventQueueFailure.setCustomerId(new CustomerId(rs.getInt("customer_id")));
        emailEventQueueFailure.setEmailSource(EmailSource.valueOf(rs.getString("source")));
        return emailEventQueueFailure;
    };
}

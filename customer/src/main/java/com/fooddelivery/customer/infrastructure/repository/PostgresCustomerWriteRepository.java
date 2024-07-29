package com.fooddelivery.customer.infrastructure.repository;

import com.fooddelivery.customer.application.exception.DuplicateCustomerException;
import com.fooddelivery.customer.application.exception.FailedToSaveCustomerException;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.constant.EmailSource;
import com.fooddelivery.customer.domain.port.output.CustomerWriteRepositoryPort;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Types;


@Repository
public class PostgresCustomerWriteRepository implements CustomerWriteRepositoryPort {
    private final Logger logger = LoggerFactory.getLogger(PostgresCustomerWriteRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public PostgresCustomerWriteRepository(@Qualifier("masterJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional("masterTransactionManager")
    public int saveCustomer(Customer customer) throws DuplicateCustomerException {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        int newCustomerId;

        try {
            jdbcTemplate.update(connection -> {
                final String sql = "INSERT INTO customers (name, surname, email) VALUES (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getSurname());
                ps.setString(3, customer.getEmail());
                return ps;
            }, keyHolder);

            newCustomerId = keyHolder.getKey().intValue();

            jdbcTemplate.update(
                    "INSERT INTO customer_preferences " +
                            "(customer_id,notification_email, notification_sms, language) " +
                            "VALUES (?,?, ?, ?)",
                    newCustomerId,
                    customer.getPreferences().isNotificationEmail(),
                    customer.getPreferences().isNotificationSms(),
                    customer.getPreferences().getLanguage()
            );
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCustomerException(customer.getEmail());
        } catch (Exception e) {
            throw new FailedToSaveCustomerException(customer.getEmail(), e.getMessage());
        }

        return newCustomerId;
    }


    public void insertFailedRegistrationEvent(CustomerId customerID, EmailSource source) {
        try {
            jdbcTemplate.update(connection -> {
                final String sql = "INSERT INTO email_event_queue_failures (customer_id, source) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, customerID.getValue());
                ps.setObject(2, source.toString(), Types.OTHER);
                return ps;
            });
        } catch (Exception e) {
            logger.error(
                    "Failed to insert the "
                    + source.toString() + " event into the database for customer ID " + customerID.getValue()
            );
        }
    }
}

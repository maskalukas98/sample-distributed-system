package com.fooddelivery.customer.infrastructure.repository;

import com.fooddelivery.customer.application.exception.CustomerNotFoundException;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.port.output.CustomerReadRepositoryPort;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresCustomerReadRepository implements CustomerReadRepositoryPort {
    private final JdbcTemplate jdbcTemplate;

    public PostgresCustomerReadRepository(@Qualifier("slaveJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer getCustomerById(CustomerId customerId) {
        try {
            String sql = "SELECT * FROM customers WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, customerRowMapper, customerId.getValue());
        } catch (EmptyResultDataAccessException e) {
            throw new CustomerNotFoundException("id", customerId.getValue().toString());
        } catch (Exception e) {
            throw e;
        }
    }

    public Customer getCustomerByEmail(String email) {
        try {
            String sql = "SELECT * FROM customers WHERE email = ?";
            return jdbcTemplate.queryForObject(sql, customerRowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw new CustomerNotFoundException("email", email);
        } catch (Exception e) {
            throw e;
        }
    }

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setId(new CustomerId(rs.getInt("id")));
        customer.setName(rs.getString("name"));
        customer.setSurname(rs.getString("surname"));
        customer.setEmail(rs.getString("email"));
        return customer;
    };
}

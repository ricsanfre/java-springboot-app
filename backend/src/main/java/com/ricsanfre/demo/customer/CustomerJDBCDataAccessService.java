package com.ricsanfre.demo.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;

    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> getAllCustomers() {

        var sql = """
                SELECT id, name, password, email, age, gender
                FROM customer
                """;
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> getCustomerById(Integer id) {

        var sql = """
                SELECT id, name, password, email, age, gender
                FROM customer
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper,id).stream().findFirst();

    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {

        var sql = """
                SELECT id, name, password, email, age, gender
                FROM customer
                WHERE email = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper,email).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {

        var sql = """
                INSERT INTO customer (name, password, email, age ,gender)
                VALUES ( ?, ?, ?, ?, ?)
                """;
        int update = jdbcTemplate.update(sql,
                customer.getName(),
                customer.getPassword(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().name());

    }

    @Override
    public boolean exitsCustomerWithEmail(String email) {
        var sql = """
                SELECT id, name, password, email, age, gender
                FROM customer
                WHERE email = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper,email).stream().findFirst().isPresent();
    }

    @Override
    public boolean exitsCustomerWithId(Integer id) {
        var sql = """
                SELECT id, name, password, email, age, gender
                FROM customer
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper,id).stream().findFirst().isPresent();
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,id);

    }

    @Override
    public void updateCustomer(Customer customer) {
        var sql = """
                UPDATE customer
                SET name = ?, password = ?, email = ?, age = ? , gender= ?
                WHERE id = ?
                """;
        jdbcTemplate.update(sql,
                customer.getName(),
                customer.getPassword(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().name(),
                customer.getId());

    }

    @Override
    public void deleteAll() {

        var sql = """
                DELETE FROM customer
                """;
        jdbcTemplate.update(sql);
    }
}

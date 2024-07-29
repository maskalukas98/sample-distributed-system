package com.fooddelivery.commerce.infrastructure.repository.order;

import com.fooddelivery.commerce.application.exception.common.DataIntegrityException;
import com.fooddelivery.commerce.application.exception.order.StatusAlreadyExistsException;
import com.fooddelivery.commerce.application.exception.product.ProductNotExistsException;
import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.configuration.datasource.ShardTransactionManagerResolver;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.entity.OrderEntity;
import com.fooddelivery.commerce.domain.order.entity.OrderStatusEntity;
import com.fooddelivery.commerce.domain.order.port.output.OrderWriteRepositoryPort;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.infrastructure.exception.product.ProductIsNotActiveException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.PreparedStatement;

@Repository
public class PostgresOrderWriterRepository extends AbstractOrderRepository implements OrderWriteRepositoryPort {
    private final ShardSelection shardRouter;
    private final ShardTransactionManagerResolver shardTransactionManager;

    PostgresOrderWriterRepository(
            ShardSelection shardRouter,
            ShardTransactionManagerResolver shardTransactionManagerResolver
    ) {
        this.shardRouter = shardRouter;
        this.shardTransactionManager = shardTransactionManagerResolver;
    }


    @Override
    public OrderId saveNewOrder(OrderAggregate orderAggregate) {
        final OrderEntity order = orderAggregate.getOrder();
        final ProductId productId = order.getProductId();
        final OrderStatusEntity currentOrderStatus = orderAggregate.getCurrentStatus();

        final PlatformTransactionManager transaction = shardTransactionManager
                                                           .getTransactionalManagerByShardKey(productId.getShardKey());
        final TransactionStatus transactionStatus = transaction.getTransaction(new DefaultTransactionDefinition());
        final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardById(productId.getShardKey());

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        long newOrderId;

        try {
            jdbcTemplate.update(connection -> {
                String sql = "INSERT INTO " + getOrdersTableWithSuffix() + " " +
                             "(customer_id, product_id, address) VALUES (?,?,?);";
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, orderAggregate.getOrder().getCustomerId());
                ps.setInt(2, order.getProductId().getValue());
                ps.setString(3, order.getAddress());
                return ps;
            }, keyHolder);

            newOrderId = keyHolder.getKey().longValue();

            jdbcTemplate.update(connection -> {
                String sql = "INSERT INTO "+ getStatusesTableWithSuffix() +" (status, order_id) VALUES (?,?);";
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, currentOrderStatus.getStatus().toString());
                ps.setLong(2, newOrderId);
                return ps;
            }, keyHolder);

        } catch (Exception e) {
            transaction.rollback(transactionStatus);
            throw e;
        }

        try {
            final Boolean isProductActive = jdbcTemplate.queryForObject(
                    "SELECT is_active FROM products WHERE id = ?",
                    (rs, rowNum) -> rs.getBoolean("is_active"),
                    productId.getValue()
            );

            if(Boolean.FALSE.equals(isProductActive)) {
                transaction.rollback(transactionStatus);
                throw new ProductIsNotActiveException(productId);
            }

        } catch (EmptyResultDataAccessException e) {
            transaction.rollback(transactionStatus);
            throw new ProductNotExistsException(productId);
        } catch (ProductIsNotActiveException e) {
            transaction.rollback(transactionStatus);
            throw new ProductIsNotActiveException(productId);
        } catch (Exception e) {
            transaction.rollback(transactionStatus);
            throw new RuntimeException(e);
        }

        transaction.commit(transactionStatus);
        return new OrderId(newOrderId);
    }


    @Override
    public void changeStatus(OrderId orderId, OrderStatus.Status newStatus) {
        final PlatformTransactionManager transaction = shardTransactionManager
                .getTransactionalManagerByShardKey(orderId.getShardKey());
        final TransactionStatus transactionStatus = transaction.getTransaction(new DefaultTransactionDefinition());
        final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardById(orderId.getShardKey());

        try {
            jdbcTemplate.update(
                    "UPDATE " + getStatusesTableWithSuffix() + ' ' +
                        "SET is_active = false " +
                        "WHERE order_id = ?;",
                    orderId.getValue()
            );

            jdbcTemplate.update(
                    "INSERT INTO " + getStatusesTableWithSuffix() + " (status, order_id) VALUES (?,?)",
                    newStatus.toString(),
                    orderId.getValue()
            );
        } catch (DuplicateKeyException e) {
            transaction.rollback(transactionStatus);

            if(e.getMessage().contains(getStatusesTableWithSuffix() + "_status_order_id_key")) {
                throw new StatusAlreadyExistsException(orderId, newStatus.toString());
            }

            throw new DataIntegrityException(e);
        } catch (Exception e) {
            transaction.rollback(transactionStatus);
            throw e;
        }

        transaction.commit(transactionStatus);
    }
}
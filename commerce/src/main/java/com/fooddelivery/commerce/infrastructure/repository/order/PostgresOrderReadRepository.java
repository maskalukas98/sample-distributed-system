package com.fooddelivery.commerce.infrastructure.repository.order;


import com.fooddelivery.commerce.application.exception.order.OrderNotExistsException;
import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.entity.OrderEntity;
import com.fooddelivery.commerce.domain.order.entity.OrderStatusEntity;
import com.fooddelivery.commerce.domain.order.port.output.OrderReadRepositoryPort;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.domain.order.valueobject.OrderStatusId;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.infrastructure.exception.db.UnableToFindShardException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class PostgresOrderReadRepository extends AbstractOrderRepository implements OrderReadRepositoryPort  {
    private final ShardSelection shardRouter;

    PostgresOrderReadRepository(ShardSelection shardRouter) {
        this.shardRouter = shardRouter;
    }

    @Override
    public OrderAggregate findOrderById(OrderId orderId) {

        try {
            final JdbcTemplate jdbcTemplate = shardRouter.selectReadShardById(orderId.getShardKey());
            final String sql = "SELECT os.id as statusId, o.*, os.* " +
                               "FROM " + getOrdersTableWithSuffix() + " as o " +
                               "JOIN " + getStatusesTableWithSuffix() + " as os ON o.id = os.order_id " +
                               "WHERE o.id = ? " +
                               "ORDER BY os.updated_at ASC";


            return jdbcTemplate.queryForObject(sql, orderRowMapper, orderId.getValue());
        } catch (EmptyResultDataAccessException | UnableToFindShardException e) {
            throw new OrderNotExistsException(orderId);
        } catch (Exception e) {
            throw e;
        }
    }

    private final RowMapper<OrderAggregate> orderRowMapper = (rs, rowNum) -> {
        final OrderId orderId = new OrderId(rs.getLong("id"));
        final OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setProductId(new ProductId(rs.getInt("product_id")));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setAddress(rs.getString("address"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        List<OrderStatusEntity> orderStatuses = new ArrayList<>();

        do {
            OrderStatusEntity orderStatus = new OrderStatusEntity();
            orderStatus.setId(new OrderStatusId(rs.getLong("statusId")));
            orderStatus.setStatus(OrderStatus.Status.valueOf(rs.getString("status")));
            orderStatus.setIsActive(rs.getBoolean("is_active"));
            orderStatus.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

            orderStatuses.add(orderStatus);
        } while (rs.next());

        return OrderAggregate.create(orderId, order, orderStatuses);
    };
}

package com.fooddelivery.commerce.helper;

import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.entity.OrderEntity;
import com.fooddelivery.commerce.domain.order.entity.OrderStatusEntity;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.domain.order.valueobject.OrderStatusId;
import com.fooddelivery.commerce.infrastructure.repository.order.AbstractOrderRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TestOrderRepositoryHelper extends AbstractOrderRepository {
    private final ShardSelection shardRouter;

    TestOrderRepositoryHelper(ShardSelection shardRouter) {
        this.shardRouter = shardRouter;
    }

    public void clearAllTables() {
        for(String c : List.of("fr","de")) {
            final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardByCountry(c);
            jdbcTemplate.update("TRUNCATE TABLE " + getOrdersTableWithSuffix() +" RESTART IDENTITY CASCADE");
        }
    }

    public List<OrderAggregate> getAllOrders(String country) {
        final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardByCountry(country);
        return jdbcTemplate.query(
                "SELECT o.*, os.id as status_id, os.* " +
                    "FROM " + getOrdersTableWithSuffix() + " as o " +
                    "JOIN " + getStatusesTableWithSuffix() + " as os ON o.id = os.order_id",
                orderAggregateRowMapper
        );
    }

    private final RowMapper<OrderAggregate> orderAggregateRowMapper = (rs, rowNum) -> {
        final OrderEntity order = new OrderEntity();
        order.setId(new OrderId(rs.getLong("id")));
        order.setAddress(rs.getString("address"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        List<OrderStatusEntity> orderStatuses = new ArrayList<>();

        do {
            OrderStatusEntity orderStatus = new OrderStatusEntity();
            orderStatus.setId(new OrderStatusId(rs.getLong("status_id")));
            orderStatus.setStatus(OrderStatus.Status.valueOf(rs.getString("status")));
            orderStatus.setIsActive(rs.getBoolean("is_active"));
            orderStatus.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

            orderStatuses.add(orderStatus);
        } while (rs.next());


        return OrderAggregate.create(order.getId(), order, orderStatuses);
    };
}

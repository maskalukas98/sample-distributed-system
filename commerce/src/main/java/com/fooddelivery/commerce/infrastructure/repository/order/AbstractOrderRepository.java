package com.fooddelivery.commerce.infrastructure.repository.order;

import java.time.YearMonth;

public abstract class AbstractOrderRepository {
    String getCurrentTableSuffix() {
        YearMonth yearMonth = YearMonth.now();
        return yearMonth.getYear() + "_" +yearMonth.getMonthValue();
    }

    public final String getOrdersTableWithSuffix() {
        return "orders_" + getCurrentTableSuffix();
    }

    public final String getStatusesTableWithSuffix() {
        return "statuses_" + getCurrentTableSuffix();
    }
}

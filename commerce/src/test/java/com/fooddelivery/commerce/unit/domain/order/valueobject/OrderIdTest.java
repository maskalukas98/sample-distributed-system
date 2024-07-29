package com.fooddelivery.commerce.unit.domain.order.valueobject;

import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
public class OrderIdTest {
    @ParameterizedTest
    @MethodSource("idsData")
    public void testGetShardKey_shouldGetShardKey(long id, int expectedShardKey) {
        // prepare
        final OrderId orderId = new OrderId(id);

        // execute
        final int shardKey = orderId.getShardKey();

        // verify
        Assertions.assertEquals(expectedShardKey, shardKey);
    }

    @ParameterizedTest
    @MethodSource("idsData")
    public void testGetTimestamp_shouldMonthAndYear(long id) {
        // prepare
        final OrderId orderId = new OrderId(id);

        // execute
        final long timestamp = orderId.getTimestamp();

        // verify
        final Date date = new Date(timestamp);
        final LocalDate localDate = date.toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
        // TODO: add more data for check of months and years
        Assertions.assertEquals(localDate.getMonthValue(), 10);
        Assertions.assertEquals(localDate.getYear(), 2023);
    }

    private static Stream<Arguments> idsData() {
        return Stream.of(
                Arguments.of(2009121654047745L, 1),
                Arguments.of(2009121654047747L, 1),
                Arguments.of(2009121654047749L, 1),
                Arguments.of(1929088990185473L, 2),
                Arguments.of(1929088990185475L, 2),
                Arguments.of(1929088990185477L, 2)
        );
    }
}

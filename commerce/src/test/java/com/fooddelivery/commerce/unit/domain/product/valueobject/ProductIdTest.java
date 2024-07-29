package com.fooddelivery.commerce.unit.domain.product.valueobject;



import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
public class ProductIdTest {
    @ParameterizedTest
    @MethodSource("idsData")
    public void testValidate_shouldValidateId(int id, int expectedShardKey, int expectedSeqId) {
        // prepare
        final ProductId productId = new ProductId(id);

        // execute
        final int seqId = productId.getSeqId();
        final int shardKey = productId.getShardKey();

        // verify
        Assertions.assertEquals(expectedSeqId, seqId);
        Assertions.assertEquals(expectedShardKey, shardKey);
    }

    private static Stream<Arguments> idsData() {
        return Stream.of(
                Arguments.of(16777217,1, 1),
                Arguments.of(16777218,1, 2),
                Arguments.of(16777219,1, 3),
                Arguments.of(33554433,2, 1),
                Arguments.of(33554434,2, 2),
                Arguments.of(33554435,2, 3)
        );
    }
}

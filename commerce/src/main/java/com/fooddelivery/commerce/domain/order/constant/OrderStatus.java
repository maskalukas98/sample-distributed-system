package com.fooddelivery.commerce.domain.order.constant;

public class OrderStatus {

    public enum Status {
        created(1),
        delivering(2),
        delivered(3),
        cancelled(4);

        private final int index;

        Status(int index) {
            this.index = index;
        }
    }

    public enum Event {
        deliver,
        complete,
        cancel
    }
}


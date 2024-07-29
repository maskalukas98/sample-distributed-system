package com.fooddelivery.customer.common.http;

import java.util.ArrayList;

public class ErrorResponse {
    public final ArrayList<String> errors = new ArrayList<>();

    public ErrorResponse(String m) {
        errors.add(m);
    }
}

package com.fooddelivery.commerce.common.http;


import java.util.ArrayList;

public class ErrorResponse {
    public final ArrayList<String> errors = new ArrayList<>();

    public ErrorResponse(String m) {
        errors.add(m);
    }
}

package com.eazybytes.gatewayserver.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.UUID;

public class FilterUtility {

    public static final String CORRELATION_ID = "eazybank-correlation-id";

    public static String getCorrelationId(HttpHeaders reqHeaders) {
        return reqHeaders.getFirst(CORRELATION_ID);
    }

    public static ServerWebExchange setRequestHeader(ServerWebExchange exchange, String key, String value) {
        return exchange.mutate()
                .request(exchange.getRequest().mutate().header(key, value).build())
                .build();
    }

    public static ServerWebExchange setCorrelationId(ServerWebExchange exchange, UUID correlationId) {
        return FilterUtility.setRequestHeader(exchange, CORRELATION_ID, correlationId.toString());
    }
}

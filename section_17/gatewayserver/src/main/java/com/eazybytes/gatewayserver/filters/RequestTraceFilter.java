package com.eazybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders reqHeaders = exchange.getRequest()
                .getHeaders();
        if (isCorrelationIdPresent(reqHeaders)) {
            logger.debug("eazybank-correlation-id found in RequestTraceFilter: {}", FilterUtility.getCorrelationId(reqHeaders));
        } else {
            UUID correlationId = generateCorrelationId();
            exchange = FilterUtility.setCorrelationId(exchange, correlationId);
            logger.debug("eazybank-correlation-id generated in RequestTraceFilter: {}", correlationId);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders reqHeaders) {
        return reqHeaders.containsKey(FilterUtility.CORRELATION_ID);
    }

    private UUID generateCorrelationId() {
        return UUID.randomUUID();
    }
}

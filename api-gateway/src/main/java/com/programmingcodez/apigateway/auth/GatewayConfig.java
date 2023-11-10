package com.programmingcodez.apigateway.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHystrix
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.programmingcodez.apigateway.auth")
public class GatewayConfig {

    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/product/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/api/order")
                        .filters(f -> f.filter(filter))
                        .uri("lb://order-service"))
                .route("tracking-service", r -> r.path("/api/tracking")
                        .filters(f -> f.filter(filter))
                        .uri("lb://tracking-service"))
                .route("delivery-service", r -> r.path("/api/delivery")
                        .filters(f -> f.filter(filter))
                        .uri("lb://delivery-service"))
                .build();
    }
}


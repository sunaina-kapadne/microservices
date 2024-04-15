package com.microservices.demo.project.orderservice.service;

import com.microservices.demo.project.orderservice.dto.InventoryResponse;
import com.microservices.demo.project.orderservice.event.OrderPlacedEvent;
import com.microservices.demo.project.orderservice.model.Order;
import com.microservices.demo.project.orderservice.model.OrderLineItems;
import com.microservices.demo.project.orderservice.repository.OrderRepository;
import com.microservices.demo.project.orderservice.dto.OrderRequest;
import com.microservices.demo.project.orderservice.dto.OrderLineItemsDto;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final ApplicationEventPublisher applicationEventPublisher;

    //Kafka
    private final ObservationRegistry observationRegistry;


    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Kafka
        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        return inventoryServiceObservation.observe(() -> {

            // Distributed Tracing
            Span inventoryServiceLookup = tracer.nextSpan().name("inventoryServiceLookup").tag("orderNumber", order.getOrderNumber());
            try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {

                // Call Inventory Service, and place order if product is in
                // stock
                InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                        .uri("http://inventory-service/api/inventory",
                                uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                        .retrieve()
                        .bodyToMono(InventoryResponse[].class)
                        .block();

                assert inventoryResponseArray != null;
                boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                        .allMatch(InventoryResponse::isInStock);

                if (allProductsInStock) {
                    orderRepository.save(order);
                    log.info("Order Placed Successfully");
                    // publish Order Placed Event
                    applicationEventPublisher.publishEvent(new OrderPlacedEvent(this, order.getOrderNumber()));
                    return "Order Placed";
                } else {
                    throw new IllegalArgumentException("Product is not in stock, please try again later");
                }

            } finally {
                inventoryServiceLookup.end();
            }
        });
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}

package com.microservices.demo.project.inventoryservice.service;

import com.microservices.demo.project.inventoryservice.dto.InventoryQuantityResponse;
import com.microservices.demo.project.inventoryservice.dto.InventoryResponse;
import com.microservices.demo.project.inventoryservice.repository.InventoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.SneakyThrows;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        // Timeout test case for servcies using this service
        /*
        log.info("Wait Started");
        Thread.sleep(10000);
        log.info("Wait Finished");
        */
        log.info("Checking Inventory");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryQuantityResponse> getAllInventory() {
        log.info("Checking Inventory");
        return inventoryRepository.findAll().stream().map(
                inventory ->
                        InventoryQuantityResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .quantity(inventory.getQuantity())
                            .build()
        ).toList();
    }
}

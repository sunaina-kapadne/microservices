package com.microservices.demo.project.inventoryservice.controller;

import com.microservices.demo.project.inventoryservice.dto.InventoryQuantityResponse;
import com.microservices.demo.project.inventoryservice.dto.InventoryResponse;
import com.microservices.demo.project.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    // http://localhost:8082/api/inventory?skuCode=iphone_13&skuCode=iphone_13_red
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        log.info("Received inventory check request for skuCode: {}", skuCode);
        return inventoryService.isInStock(skuCode);
    }

    // http://localhost:8082/api/inventory
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryQuantityResponse> getAllInventory() {
        log.info("All inventories with stock");
        return inventoryService.getAllInventory();
    }
}

package com.microservices.demo.project.orderservice.repository;

import com.microservices.demo.project.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

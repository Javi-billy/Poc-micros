package com.example.demo.infrastructure.adapter.out.feign;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-inventario", url = "${clients.inventario.url}")
public interface InventoryFeignClient {

    @PostMapping("/api/inventory/{productCode}/decrease")
    void decreaseStock(@PathVariable("productCode") String productCode,
                       @RequestParam("quantity") int quantity);
}
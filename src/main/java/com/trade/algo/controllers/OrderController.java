package com.trade.algo.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @PostMapping("/order")
    public String placeOrder()
    {
        return "Order Placed Successfully";
    }
}

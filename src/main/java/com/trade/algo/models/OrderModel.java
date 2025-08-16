package com.trade.algo.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderModel {
    private String userId;
    private Double price;
    private int quantity;
}

package com.trade.algo.requests;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {
    private String side;
    private Double price;
    private Integer quantity;
    private String userId;
}

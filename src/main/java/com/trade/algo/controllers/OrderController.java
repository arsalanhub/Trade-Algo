package com.trade.algo.controllers;

import com.trade.algo.models.OrderModel;
import com.trade.algo.requests.OrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class OrderController {
    // Buyer - Green
    private List<OrderModel> bids=new ArrayList<>();

    // Seller - Red
    private List<OrderModel> asks=new ArrayList<>();

    private Map<String, Double> users=new HashMap<>(
            Map.of(
                    "123", 500.00,
                    "456", 650.50
            )
    );

    @PostMapping("/order")
    public String placeOrder(@RequestBody OrderRequest orderRequest)
    {
        int remainingQty=fillOrders(orderRequest.getSide(), orderRequest.getPrice(),
                                    orderRequest.getQuantity(), orderRequest.getUserId());
        if(remainingQty==0) return "All Orders Filled";

        // Add Quantity which is not able to Fulfilled in Order Book
        if(orderRequest.getSide().equals("bid"))
        {
            bids.add(new OrderModel(orderRequest.getUserId(), orderRequest.getPrice(),
                                    remainingQty));

            // Sort Ascending
            bids.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
        }
        else
        {
            asks.add(new OrderModel(orderRequest.getUserId(), orderRequest.getPrice(),
                    remainingQty));

            // Sort Descending
            asks.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
        }
        return String.format("Quantity Filled - %d", orderRequest.getQuantity()-remainingQty);
    }

    // Removing balance from one user & adding it to another user
    public void FlipBalance(String user1, String user2, int quantity, Double price)
    {
        users.put(user1, users.getOrDefault(user1, 0.0)+(price*quantity));
        users.put(user2, users.getOrDefault(user2, 0.0)-(price*quantity));
    }

    public int fillOrders(String side, Double price, int quantity, String userId)
    {
        int remQty=quantity;
        if(side.equals("bid"))
        {
            for(int i=asks.size()-1; i>=0; i--)
            {
                var order=asks.get(i);
                if(order.getPrice()>price || remQty==0) break;
                else if(order.getQuantity()>remQty)
                {
                    var qtyLeft=order.getQuantity()-remQty;

                    // Balance will be deducted from buyer & credited to seller
                    FlipBalance(order.getUserId(), userId, remQty, order.getPrice());

                    asks.get(i).setQuantity(qtyLeft);
                    remQty=0;
                }
                else
                {
                    remQty=remQty-order.getQuantity();

                    // Balance will be deducted from buyer & credited to seller
                    FlipBalance(order.getUserId(), userId, order.getQuantity(), order.getPrice());

                    asks.remove(asks.size()-1);
                }
            }
        }
        else if(side.equals("ask"))
        {
            for(int i=bids.size()-1; i>=0; i--)
            {
                var order=bids.get(i);
                if(order.getPrice()<price || remQty==0) break;
                if(order.getQuantity()>remQty)
                {
                    // balance will be credited to seller & deducted from the buyer
                    FlipBalance(userId, order.getUserId(), remQty, order.getPrice());
                    var qtyLeft=order.getQuantity()-remQty;
                    bids.get(i).setQuantity(qtyLeft);
                    remQty=0;
                }
                else
                {
                    // balance will be credited to seller & deducted from the buyer
                    FlipBalance(userId, order.getUserId(), order.getQuantity(), order.getPrice());
                    remQty-=order.getQuantity();
                    bids.remove(bids.size()-1);
                }
            }
        }
        return remQty;
    }
}

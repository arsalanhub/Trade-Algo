package com.trade.algo;

import com.trade.algo.controllers.OrderController;
import com.trade.algo.requests.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderController orderController;

    /*@Test
    void testPlaceOrder() throws Exception {
        mockMvc.perform(post("/order"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order Placed Successfully"));
    }*/

    @Test
    void testAllOrdersFilledWithMockedFillOrders() throws Exception {
        OrderController spyController = spy(orderController);

        doReturn(0).when(spyController).fillOrders("bid", 100.14, 2, "123");

        OrderRequest bidOrder = new OrderRequest("bid", 100.14, 2, "123");

        String response = spyController.placeOrder(bidOrder);

        assertThat(response).isEqualTo("All Orders Filled");
    }

    @Test
    void testBidAndAskOrder() throws Exception {
        OrderController spyController = spy(orderController);
        OrderRequest bidRequest=new OrderRequest("bid", 150.50, 3, "123");

        String response = spyController.placeOrder(bidRequest);
        assertThat(response).isEqualTo("Quantity Filled - 0");

        OrderRequest askRequest=new OrderRequest("ask", 100.00, 5, "456");
        response = spyController.placeOrder(askRequest);
        assertThat(response).isEqualTo("Quantity Filled - 3");

        askRequest=new OrderRequest("ask", 90.00, 4, "789");
        response = spyController.placeOrder(askRequest);
        assertThat(response).isEqualTo("Quantity Filled - 0");

        bidRequest=new OrderRequest("bid", 100.00, 7, "789");
        response = spyController.placeOrder(bidRequest);
        assertThat(response).isEqualTo("Quantity Filled - 6");
    }
}

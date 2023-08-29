package com.duwei.summer.rpc;

import com.duwei.interfece.OrderService;
import com.duwei.summer.rpc.config.ReferenceConfig;

import java.io.IOException;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 18:48
 * @since: 1.0
 */
public class Application {
    public static void main(String[] args) throws IOException {
        ReferenceConfig<OrderService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceRef(OrderService.class);
        Bootstrap.getInstance().reference(referenceConfig);
        OrderService orderService = referenceConfig.get();
        System.out.println(orderService.getOrderCount());
    }
}

package com.duwei.summer.rpc.impl;

import com.duwei.interfece.OrderService;
import com.duwei.summer.rpc.annotation.RpcService;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 19:55
 * @since: 1.0
 */
@RpcService
public class OrderServiceOneImpl implements OrderService {
    @Override
    public String getOrderDesc() {
        return "服务1的订单服务";
    }

    @Override
    public int getOrderCount() {
        return 1;
    }

    public static void main(String[] args) {

    }
}

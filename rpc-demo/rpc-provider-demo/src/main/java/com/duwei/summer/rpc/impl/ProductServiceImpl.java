package com.duwei.summer.rpc.impl;

import com.duwei.interfece.ProductService;
import com.duwei.summer.rpc.annotation.RpcService;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 19:58
 * @since: 1.0
 */
@RpcService
public class ProductServiceImpl implements ProductService {
    @Override
    public String getProductService() {
        return "服务1的商品服务";
    }

    @Override
    public double getProductPrice() {
        return 22.33;
    }
}

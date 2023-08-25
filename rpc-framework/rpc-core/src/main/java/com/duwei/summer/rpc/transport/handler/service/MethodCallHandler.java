package com.duwei.summer.rpc.transport.handler.service;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.transport.message.request.RequestPayload;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>
 * 处理方法调用
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 11:07
 * @since: 1.0
 */
@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        // 1.获取负载
        RequestPayload requestPayload = rpcRequest.getRequestPayload();

        // 2.根据负载内容进行方法调用
        Object object = callTargetMethod(requestPayload);

        // 3.封装响应

        // 4.写出响应
    }

    private Object callTargetMethod(RequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String methodName = requestPayload.getMethodName();
        Class<?>[] parametersType = requestPayload.getParametersType();
        Object[] parametersValue = requestPayload.getParametersValue();
        ServiceConfig<?> serviceConfig = null;
        Object refImpl = serviceConfig.getRef();


        // 1.获取方法对象
        try {
            Method method = refImpl.getClass().getMethod(methodName, parametersType);
            return method.invoke(refImpl, parametersValue);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("调用服务{}的方法{}出现异常", interfaceName, methodName);
        }
        return null;
    }
}

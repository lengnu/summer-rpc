package com.duwei.summer.rpc.transport.handler.service;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.compress.CompressorType;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.serialize.SerializerType;
import com.duwei.summer.rpc.transport.message.request.RequestPayload;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import com.duwei.summer.rpc.transport.message.response.ResponseCode;
import com.duwei.summer.rpc.transport.message.response.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class MethodCallHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        // 1.获取负载
        RequestPayload requestPayload = rpcRequest.getRequestPayload();

        // 2.根据负载内容进行方法调用
         RpcResponse rpcResponse = callTargetMethod(rpcRequest,requestPayload);

        // 3.写出响应
        log.info("请求处理完成，开始进行响应，客户端ip{}，客户端请求接口{}",
                channelHandlerContext.channel().remoteAddress(),
                rpcRequest.getRequestPayload().getInterfaceName());
        channelHandlerContext.channel().writeAndFlush(rpcResponse).addListener((ChannelFutureListener) channelFuture -> applicationContext.finishRequest());
    }

    private RpcResponse callTargetMethod(RpcRequest rpcRequest,RequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String methodName = requestPayload.getMethodName();
        Class<?>[] parametersType = requestPayload.getParametersType();
        Object[] parametersValue = requestPayload.getParametersValue();
        ServiceConfig<?> serviceConfig = applicationContext.getService(interfaceName);
        Object ref = serviceConfig.getRef();

        // 1.获取方法对象
        try {
            Method method = ref.getClass().getMethod(methodName, parametersType);
            Object result = method.invoke(ref, parametersValue);
            System.out.println("resu;t ++++++++++++++++++++");
            return RpcResponse.build(rpcRequest,ResponseCode.SUCCESS,result);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("调用服务{}的方法{}出现异常", interfaceName, methodName);
            return RpcResponse.build(rpcRequest,ResponseCode.SERVER_ERROR,null);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext application) {
        this.applicationContext = application;
    }
}

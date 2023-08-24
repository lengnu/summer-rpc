package com.duwei.summer.rpc.context;

import com.duwei.summer.rpc.compress.Compressor;
import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.context.xml.XmlReader;
import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import com.duwei.summer.rpc.loadbalance.impl.RoundRobinLoadBalancer;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.serialize.Serializer;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.uid.IdWorker;
import com.duwei.summer.rpc.uid.SnowflakeIdWorker;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *  上下文配置类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 10:46
 * @since: 1.0
 */
@Data
@NoArgsConstructor
public class Configuration {
    /**
     * 端口号
     */
    private int port = 9999;
    /**
     * 应用程序名称
     */
    private String application = "default";
    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig("zookeeper://127.0.0.1:2181");
    /**
     * ID生成器
     */
    private IdWorker idWorker = new SnowflakeIdWorker(1,2);
    /**
     * 负载均衡配置
     */
    private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
    /**
     * 序列化协议
     */
    private String serializeName = "hessian";
    /**
     * 压缩协议
     */
    private String compressName = "gzip";
    /**
     * 序列化器
     */
    private Serializer serializer;
    /**
     * 压缩器
     */
    private Compressor compressor;
    /**
     * 限流器
     */

    private static final long DEFAULT_WAIT_RESPONSE_TIMEOUT = 1000L;
    /**
     * 记录获取响应的最大超时时间
     */
    private long waitResponseTimeout = DEFAULT_WAIT_RESPONSE_TIMEOUT;


    public void flush(){
        if (serializer == null){
            serializer = SerializerFactory.getSerializerWrapper(serializeName).getSerializer();
        }

        if (compressor == null){
            compressor = CompressorFactory.getCompressorWrapper(compressName).getCompressor();
        }
    }

    public Configuration(String resource) {
        XmlReader xmlReader = new XmlReader();
        xmlReader.load(resource,this);
        flush();
    }
}

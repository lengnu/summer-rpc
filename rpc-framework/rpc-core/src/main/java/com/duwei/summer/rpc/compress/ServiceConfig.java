package com.duwei.summer.rpc.compress;

/**
 * <p>
 * 服务配置
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 21:54
 * @since: 1.0
 */
public class ServiceConfig<T> {
    private Class<T> interfaceProvider;
    private Object ref;

    public void setInterface(Class<T> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public Class<T> getInterfaceProvider() {
        return interfaceProvider;
    }

    public Object getRef() {
        return ref;
    }
}

package com.duwei.summer.rpc.close;

import com.duwei.summer.rpc.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * 关闭时的钩子函数
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-28 09:12
 * @since: 1.0
 */
@Slf4j
public class ShutdownHock extends Thread {
    private final ApplicationContext applicationContext;


    public ShutdownHock(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        applicationContext.close();
    }
}

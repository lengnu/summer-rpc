package com.duwei.summer.rpc.retry;

import com.duwei.summer.rpc.annotation.Retry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public  class RetryPolicyMetadataHolder {
        private final int retryTimes;
        private final int tickTimes;
        private Constructor<RetryPolicy> constructor;

        @SuppressWarnings("unchecked")
        public RetryPolicyMetadataHolder(Retry retry) {
            this.retryTimes = retry.retryTimes();
            this.tickTimes = retry.tickTimes();
            try {
                this.constructor = (Constructor<RetryPolicy>) retry.retryPolicy().getConstructor(int.class, int.class);
            } catch (NoSuchMethodException ignored) {
            }
        }

        public RetryPolicy getRetryPolicy() {
            try {
                return constructor.newInstance(retryTimes, tickTimes);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return new FixedIntervalRetryPolicy(0, 0);
            }
        }

        @SuppressWarnings("unchecked")
        public RetryPolicyMetadataHolder(int retryTimes, int tickTimes, Class<? extends RetryPolicy> retryPolicyClass) {
            this.retryTimes = retryTimes;
            this.tickTimes = tickTimes;
            try {
                this.constructor = (Constructor<RetryPolicy>) retryPolicyClass.getConstructor(int.class, int.class);
            } catch (NoSuchMethodException ignored) {

            }
        }
    }
package com.duwei.summer.rpc.uid;

/**
 * ID生成器-采用雪花算法
 * <p>
 * 雪花算法结构：
 * 1bit      41bit      10bit      12bit
 * 未使用     时间戳     工作机器ID     序列号
 * <p>
 * 该类用于生成分布式唯一ID，支持自定义工作机器ID。
 * <p>
 * 注意事项：
 * 1. 工作机器ID的合法取值范围为0~1023（对应10个比特位）。
 * 2. 当发生时钟回拨时，会抛出异常。
 * 3. 在实际使用中，请根据具体环境和需求进行调整和优化。
 * <p>
 * 示例用法：
 * IdWorker idWorker = new IdWorker(1); //
 * long id = idWorker.nextId();
 */
public class SnowflakeIdWorker implements IdWorker {
    /**
     * 起始timestamp
     */
    private static final long START_TIMESTAMP = 1630454400000L;

    /**
     * 数据中心ID的比特位数
     */
    public static final int DATA_CENTER_ID_BITS = 5;
    /**
     * 工作机器ID的比特位数
     */
    public static final int WORKER_ID_BITS = 5;
    /**
     * 序列号的比特位数
     */
    public static final int SEQUENCE_BITS = 12;

    /**
     * 机器ID偏移量
     */
    public static final int WORKER_ID_OFFSET = SEQUENCE_BITS;
    /**
     * 数据中心ID偏移量
     */
    public static final int DATA_CENTER_ID_OFFSET = WORKER_ID_BITS + SEQUENCE_BITS;
    /**
     * 时间戳偏移量
     */
    public static final int TIMESTAMP_OFFSET = DATA_CENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS;

    /**
     * 工作机器ID最大值
     */
    public static final long WORKER_ID_MAX = ~(-1L << WORKER_ID_BITS);
    /**
     * 数据中心ID最大值
     */
    public static final long DATA_CENTER_ID_MAX = ~(-1L << DATA_CENTER_ID_BITS);
    /**
     * 序列号的掩码，超过该值需要等待到下一秒生成ID
     */
    public static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private final long workerId;
    private final long dataCenterId;
    private long sequenceId;
    private long lastTimestamp = -1L;
    private final Object lock = new Object();

    public SnowflakeIdWorker(long workerId, long dataCenterId) {
        if (workerId > WORKER_ID_MAX || workerId < 0) {
            throw new IllegalArgumentException("工作机器ID不合法");
        }
        if (dataCenterId > DATA_CENTER_ID_MAX || dataCenterId < 0) {
            throw new IllegalArgumentException("数据中心ID不合法");
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    @Override
    public long nextId() {
        synchronized (lock) {
            long currentTimestamp = timeGen();

            // 判断时钟回拨
            if (currentTimestamp < lastTimestamp) {
                throw new RuntimeException("您的服务器进行了时钟回拨");
            }

            // sequenceId
            if (currentTimestamp == lastTimestamp) {
                sequenceId = (sequenceId + 1) & SEQUENCE_MASK;
                if (sequenceId == 0) {
                    currentTimestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequenceId = 0;
            }
            lastTimestamp = currentTimestamp;

            // 生成ID
            return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_OFFSET)
                    | (dataCenterId << DATA_CENTER_ID_OFFSET)
                    | (workerId << WORKER_ID_OFFSET)
                    | sequenceId;
        }
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

}
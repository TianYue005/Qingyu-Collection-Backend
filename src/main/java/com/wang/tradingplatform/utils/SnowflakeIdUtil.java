package com.wang.tradingplatform.utils;

import org.springframework.stereotype.Component;

/**
 * 雪花算法分布式ID生成器
 *
 * ID结构（64位）：
 *   1位符号位（始终为0）
 *  41位时间戳（毫秒级，可用约69年）
 *  10位工作机器ID（5位数据中心ID + 5位工作节点ID）
 *  12位序列号（同一毫秒内最多生成4096个ID）
 *
 * @since 1.0
 */
@Component
public class SnowflakeIdUtil {

    /*起始时间戳（2025-01-01 00:00:00）*/
    private static final long START_TIMESTAMP = 1735689600000L;

    /*各部分占用的位数*/
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    /*各部分最大值*/
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /*各部分向左位移量*/
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /*数据中心ID*/
    private final long dataCenterId;

    /*工作节点ID*/
    private final long workerId;

    /*毫秒内序列号*/
    private long sequence = 0L;

    /*上次生成ID的时间戳*/
    private long lastTimestamp = -1L;

    /**
     * 使用默认的数据中心ID和工作节点ID初始化（均为0），适用于单机部署场景
     */
    public SnowflakeIdUtil() {
        this(0, 0);
    }

    /**
     * 指定数据中心ID和工作节点ID初始化，适用于分布式多节点部署场景
     *
     * @param dataCenterId 数据中心ID（0 ~ 31）
     * @param workerId     工作节点ID（0 ~ 31）
     * @throws IllegalArgumentException 当参数超出合法范围时抛出
     */
    public  SnowflakeIdUtil(long dataCenterId, long workerId) {
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    "数据中心ID超出范围，合法值为 0 ~ " + MAX_DATA_CENTER_ID
            );
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    "工作节点ID超出范围，合法值为 0 ~ " + MAX_WORKER_ID
            );
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个唯一ID
     *
     * 核心逻辑：
     * 1. 获取当前时间戳
     * 2. 处理时钟回拨异常
     * 3. 同一毫秒内序列号自增，若用完则等待到下一毫秒
     * 4. 按位运算组装最终ID
     *
     * @return 全局唯一的64位长整型ID
     * @throws RuntimeException 当系统时钟发生回拨时抛出
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        /*时钟回拨检测*/
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    "系统时钟回拨，拒绝生成ID，回拨毫秒数：" + (lastTimestamp - currentTimestamp)
            );
        }

        /*同一毫秒内序列号递增*/
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            /*当前毫秒序列号已用完，等待到下一毫秒*/
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            /*不同毫秒，序列号重置为0*/
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        /*按位运算组装ID：时间戳 + 数据中心ID + 工作节点ID + 序列号*/
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞直到下一毫秒，解决当前毫秒内序列号耗尽问题
     *
     * @param lastTimestamp 上一次生成ID的时间戳
     * @return 大于 lastTimestamp 的当前时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}

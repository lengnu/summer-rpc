package com.duwei.summer.rpc.transport.message;

/**
 * <p>
 * 消息封装的常量类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 20:06
 * @since: 1.0
 */
public class MessageFormatConstant {
    /**
     * 魔数
     */
    public final static int MAGIC = 0x12345678;
    /**
     * 魔数长度
     */
    public static final int MAGIC_LENGTH = 4;
    /**
     * 版本号
     */
    public final static byte VERSION = 1;
    /**
     * 版本号长度
     */
    public static final int VERSION_LENGTH = 1;
    /**
     * 长度字段长度
     */
    public static final int LENGTH_FIELD_LENGTH = 4;
    /**
     * 请求类型
     */
    private static final int REQUEST_TYPE_LENGTH = 1;
    /**
     * 序列化类型长度
     */
    public static final int SERIALIZE_TYPE_LENGTH = 1;
    /**
     * 压缩类型长度
     */
    public static final int COMPRESS_TYPE_LENGTH = 1;
    /**
     * 请求ID长度
     */
    public static final int REQUEST_ID_LENGTH = 8;
    /**
     * 时间戳长度
     */
    public static final int TIMESTAMP_LENGTH = 8;
    /**
     * 状态码长度
     */
    public static final int RESPONSE_CODE_LENGTH = 1;
    /**
     * 请求首部总长度
     */
    public final static byte REQUEST_HEADER_TOTAL_LENGTH =
            MAGIC_LENGTH +
                    VERSION_LENGTH +
                    LENGTH_FIELD_LENGTH +
                    REQUEST_TYPE_LENGTH +
                    SERIALIZE_TYPE_LENGTH +
                    COMPRESS_TYPE_LENGTH +
                    REQUEST_ID_LENGTH +
                    TIMESTAMP_LENGTH;
    /**
     * 响应首部总长度
     */
    public final static byte RESPONSE_HEADER_TOTAL_LENGTH =
            MAGIC_LENGTH +
                    VERSION_LENGTH +
                    LENGTH_FIELD_LENGTH +
                    REQUEST_TYPE_LENGTH +
                    SERIALIZE_TYPE_LENGTH +
                    COMPRESS_TYPE_LENGTH +
                    REQUEST_ID_LENGTH +
                    TIMESTAMP_LENGTH;
    /**
     * 最大帧长度
     */
    public static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    /**
     * 长度字段偏移
     */
    public static final int LENGTH_FIELD_OFFSET = MAGIC_LENGTH + VERSION_LENGTH;


}

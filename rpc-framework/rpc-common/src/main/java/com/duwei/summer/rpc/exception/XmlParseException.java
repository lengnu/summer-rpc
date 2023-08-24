package com.duwei.summer.rpc.exception;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 15:14
 * @since: 1.0
 */
public class XmlParseException extends RuntimeException{
    public XmlParseException() {
    }

    public XmlParseException(String message) {
        super(message);
    }

    public XmlParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlParseException(Throwable cause) {
        super(cause);
    }
}

package com.duwei.summer.rpc.context.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *  将网络的dtd映射到本地
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 14:53
 * @since: 1.0
 */
public class LocalDtdEntityResolver implements EntityResolver{
    private static final String DTD_LOCAL_LOCATION = "com.duwei.summer.rpc.context.xml.rpc-config.dtd";
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(DTD_LOCAL_LOCATION);
        return new InputSource(resourceAsStream);
    }
}

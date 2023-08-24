package com.duwei.summer.rpc.context.xml;

import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.context.Configuration;
import com.duwei.summer.rpc.exception.XmlParseException;
import com.duwei.summer.rpc.serialize.Serializer;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.duwei.summer.rpc.util.StringUtils.*;

/**
 * <p>
 * 读取配置文件
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 11:09
 * @since: 1.0
 */
@Slf4j
public class XmlReader {

    private static final String SERIALIZER = "serializer";
    private static final String COMPRESSOR = "compressor";

    public void load(String resource,Configuration configuration) {
        try {
            // 1.构建document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new LocalDtdEntityResolver());
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
            Document document = documentBuilder.parse(inputStream);
            // 2.解析表达式
            parseDocument(document);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("xml解析异常");
            throw new XmlParseException("xml解析异常",e);
        }
    }

    private void parseDocument(Document document) {
        Configuration configuration = new Configuration();
        parse(document.getDocumentElement(), configuration);
    }

    private void parse(Element root, Configuration configuration) {
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element) {
                parseDispatch((Element) item, configuration);
            }
        }
    }

    private void parseDispatch(Element element, Configuration configuration) {
        if (element.getTagName().equals(SERIALIZER)) {
            parseSerializer(element, configuration);
            return;
        }

        if (element.getTagName().equals(COMPRESSOR)) {
            parseCompressor(element, configuration);
        }
    }


    private void parseSerializer(Element element, Configuration configuration) {
        String type = element.getAttribute("type");
        String tClass = element.getAttribute("class");
        if (!isBlank(tClass) && !isBlank(tClass)) {
            log.error("xml 解析出错,{} 元素下 type 和 class 只能出现一个", element.getTagName());
            throw new XmlParseException("xml 解析出错," + element.getTagName() + " 元素下 type 和 class 只能出现一个");
        }
        if (!isBlank(tClass)) {
            try {
                Class<?> aClass = Class.forName(tClass);
                Constructor<?> constructor = aClass.getConstructor();
                constructor.setAccessible(true);
                Object serializer = constructor.newInstance();
                configuration.setSerializer((Serializer) serializer);
            } catch (ClassNotFoundException e) {
                log.error("未找到对应的序列化类 {}", tClass);
                throw new XmlParseException("未找到对应的序列化类", e);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                log.error("请检查对应的类是否存在默认构造方法");
                throw new XmlParseException("构造方法访问异常", e);
            }
            return;
        }
        SerializerWrapper serializerWrapper = SerializerFactory.getSerializerWrapper(type);
        configuration.setSerializer(serializerWrapper.getSerializer());
    }

    private void parseCompressor(Element element, Configuration configuration) {
        String type = element.getAttribute("type");
        String tClass = element.getAttribute("class");
        if (!isBlank(tClass) && !isBlank(tClass)) {
            log.error("xml 解析出错,{} 元素下 type 和 class 只能出现一个", element.getTagName());
            throw new XmlParseException("xml 解析出错," + element.getTagName() + " 元素下 type 和 class 只能出现一个");
        }
        if (!isBlank(tClass)) {
            try {
                Class<?> aClass = Class.forName(tClass);
                Constructor<?> constructor = aClass.getConstructor();
                constructor.setAccessible(true);
                Object serializer = constructor.newInstance();
                configuration.setSerializer((Serializer) serializer);
            } catch (ClassNotFoundException e) {
                log.error("未找到对应的压缩器类 {}", tClass);
                throw new XmlParseException("未找到对应的压缩器类", e);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                log.error("请检查对应的类是否存在默认构造方法");
                throw new XmlParseException("构造方法访问异常", e);
            }
            return;
        }
        CompressorWrapper compressorWrapper = CompressorFactory.getCompressorWrapper(type);
        configuration.setCompressor(compressorWrapper.getCompressor());
    }


}

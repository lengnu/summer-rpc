package com.duwei.summer.rpc.context.xml;

import com.duwei.summer.rpc.compress.Compressor;
import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.compress.CompressorRegister;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.config.BaseConfig;
import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.exception.SerializeException;
import com.duwei.summer.rpc.exception.XmlParseException;
import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfig;
import com.duwei.summer.rpc.registry.Registry;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.serialize.Serializer;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.serialize.SerializerRegister;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.uid.IdGenerator;
import com.duwei.summer.rpc.uid.IdGeneratorConfig;
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

import static com.duwei.summer.rpc.util.StringUtils.isBlank;

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
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String APPLICATION = "application";
    private static final String TIMEOUT = "timeout";
    private static final String EARLY_CONNECT = "earlyConnect";

    private static final String SERIALIZER = "serializer";
    private static final String COMPRESSOR = "compressor";
    private static final String CLASS = "class";
    private static final String NAME = "name";

    // private static final String PROPERTY = "properties";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    private static final String REGISTER = "register";
    private static final String LOAD_BALANCER = "loadbalancer";
    private static final String ID_GENERATOR = "idGenerator";

    private final ApplicationContext applicationContext;

    public XmlReader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void load(String resource) {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (resourceAsStream != null) {
            try {
                // 1.构建document
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                documentBuilder.setEntityResolver(new LocalDtdEntityResolver());
                Document document = documentBuilder.parse(resourceAsStream);
                // 2.解析表达式
                parseDocument(document);

            } catch (ParserConfigurationException | SAXException | IOException e) {
                log.error("xml解析异常");
                throw new XmlParseException("xml解析异常", e);
            }
        }else {
            log.info("未找到配置文件，将采用默认配置");
        }
    }

    private void parseDocument(Document document) {
        parse(document.getDocumentElement());
    }

    private void parse(Element root) {
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element) {
                parseDispatch((Element) item);
            }
        }
    }

    private void parseDispatch(Element element) {
        switch (element.getTagName()) {
            case REGISTER:
                parseRegister(element);
                break;
            case LOAD_BALANCER:
                parseLoadBalancer(element);
                break;
            case ID_GENERATOR:
                parseIdGenerator(element);
                break;
            case SERIALIZER:
                parseSerializer(element);
                break;
            case COMPRESSOR:
                parseCompressor(element);
                break;
            case APPLICATION:
                parseApplication(element);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseSerializer(Element element) {
        String tClass = element.getAttribute(CLASS);
        String name = element.getAttribute(NAME);
        if (!isBlank(tClass) && !isBlank(name)) {
            log.error("xml 解析出错,{} 元素下  class 和 name只能出现一个", element.getTagName());
            throw new XmlParseException("xml 解析出错," + element.getTagName() + " 元素下  class 和 name只能出现一个");
        }
        if (!isBlank(tClass)) {
            try {
                Class<? extends Serializer> aClass = (Class<? extends Serializer>) Class.forName(tClass);
                SerializerRegister.registerSerializerIfNecessary(aClass);
                SerializerWrapper serializerWrapper = SerializerFactory.getSerializerWrapper(aClass);
                applicationContext.setSerializerWrapper(serializerWrapper);
            } catch (ClassNotFoundException e) {
                log.error("未找到对应的序列化类 {}", tClass);
                throw new XmlParseException("未找到对应的序列化类", e);
            }
            return;
        }
        if (!isBlank(name)) {
            applicationContext.setSerializerWrapper(SerializerFactory.getSerializerWrapper(name));
            return;
        }
        log.error("序列化配置异常");
        throw new SerializeException("序列化配置异常");
    }

    @SuppressWarnings("unchecked")
    private void parseCompressor(Element element) {
        String tClass = element.getAttribute(CLASS);
        String name = element.getAttribute(NAME);

        if (!isBlank(tClass) && !isBlank(name)) {
            log.error("xml 解析出错,{} 元素下  class 和 name只能出现一个", element.getTagName());
            throw new XmlParseException("xml 解析出错," + element.getTagName() + " 元素下  class 和 name只能出现一个");
        }
        if (!isBlank(tClass)) {
            try {
                Class<? extends Compressor> aClass = (Class<? extends Compressor>) Class.forName(tClass);
                CompressorRegister.registerCompressIfNecessary(aClass);
                CompressorWrapper compressorWrapper = CompressorFactory.getCompressorWrapper(aClass);
                applicationContext.setCompressorWrapper(compressorWrapper);
            } catch (ClassNotFoundException e) {
                log.error("未找到对应的压缩类 {}", tClass);
                throw new XmlParseException("未找到对应的压缩类", e);
            }
            return;
        }
        if (!isBlank(name)) {
            applicationContext.setCompressorWrapper(CompressorFactory.getCompressorWrapper(name));
            return;
        }
        log.error("压缩器配置异常");
        throw new SerializeException("压缩器配置异常");
    }


    @SuppressWarnings("unchecked")
    private void parseIdGenerator(Element element) {
        String aClass = element.getAttribute(CLASS);
        if (isBlank(aClass)) {
            log.error("解析idGeneratorConfig标签失败，请检查class属性是否存在");
            throw new XmlParseException("解析idGeneratorConfig标签失败，class属不能为null");
        }

        IdGeneratorConfig idGeneratorConfig = new IdGeneratorConfig();
        idGeneratorConfig.setApplicationContext(applicationContext);
        Class<? extends IdGenerator> idGeneratorClass;
        try {
            idGeneratorClass = (Class<? extends IdGenerator>) Class.forName(aClass);
            idGeneratorConfig.setIdGeneratorClass(idGeneratorClass);
        } catch (ClassNotFoundException e) {
            log.error("ID生成器对应的类不存在");
            throw new XmlParseException("ID生成器对应的类不存在", e);
        }
        NodeList childNodes = element.getChildNodes();
        if (childNodes.getLength() != 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                    parseProperties((Element) childNodes.item(i), idGeneratorConfig);
                }
            }

        }
        applicationContext.setIdGeneratorConfig(idGeneratorConfig);
    }

    @SuppressWarnings("unchecked")
    private void parseRegister(Element element) {
        String aClass = element.getAttribute(CLASS);
        String host = element.getAttribute(HOST);
        String port = element.getAttribute(PORT);
        if (isBlank(aClass) || isBlank(host) || isBlank(port)) {
            log.error("解析register标签失败，请检查class,host,port属性是否存在");
            throw new XmlParseException("解析register标签失败，class,host,port属不能为null");
        }

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setApplicationContext(applicationContext);
        registryConfig.setHost(host);
        Class<? extends Registry> registryClass;
        int actualPort;
        try {
            registryClass = (Class<? extends Registry>) Class.forName(aClass);
            actualPort = Integer.parseInt(port);
            registryConfig.setRegistryClass(registryClass);
            registryConfig.setPort(actualPort);
        } catch (ClassNotFoundException e) {
            log.error("注册中心对应的类不存在");
            throw new XmlParseException("注册中心对应的类不存在", e);
        } catch (ClassCastException e) {
            log.error("port解析失败");
            throw new XmlParseException("port必须为整数", e);
        }
        NodeList childNodes = element.getChildNodes();
        if (childNodes.getLength() != 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                    parseProperties((Element) childNodes.item(i), registryConfig);
                }
            }

        }
        applicationContext.setRegistryConfig(registryConfig);
    }


    /**
     * 解析负载均衡对应的配置
     */
    @SuppressWarnings("unchecked")
    private void parseLoadBalancer(Element element) {
        String aClass = element.getAttribute(CLASS);
        if (isBlank(aClass)) {
            log.error("解析loadbalancer标签失败，请检查class属性是否存在");
            throw new XmlParseException("解析loadbalancer标签失败，class属不能为null");
        }

        LoadBalancerConfig loadBalancerConfig = new LoadBalancerConfig();
        loadBalancerConfig.setApplicationContext(applicationContext);
        Class<? extends LoadBalancer> loadbalancerClass;
        try {
            loadbalancerClass = (Class<? extends LoadBalancer>) Class.forName(aClass);
            loadBalancerConfig.setLoadbalancerClass(loadbalancerClass);
        } catch (ClassNotFoundException e) {
            log.error("负载均衡器对应的类不存在");
            throw new XmlParseException("负载均衡器对应的类不存在", e);
        }
        NodeList childNodes = element.getChildNodes();
        if (childNodes.getLength() != 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                    parseProperties((Element) childNodes.item(i), loadBalancerConfig);
                }
            }

        }
        applicationContext.setLoadBalancerConfig(loadBalancerConfig);
    }

    /**
     * 解析properties属性
     *
     * @param element properties标签
     */
    private void parseProperties(Element element, BaseConfig baseConfig) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element propertyElement = (Element) node;
                String key = propertyElement.getAttribute(KEY);
                if (isBlank(key)) {
                    log.error("解析property标签失败");
                    throw new XmlParseException("property标签的key值不能为空");
                }
                String value = propertyElement.getAttribute(VALUE);
                if (isBlank(value)) {
                    log.error("解析property标签失败");
                    throw new XmlParseException("property标签的value值不能为空");
                }
                baseConfig.setAttribute(key, value);
            }
        }
    }

    private void parseApplication(Element element) {
        String port = element.getAttribute(PORT);
        String name = element.getAttribute(NAME);
        String timeout = element.getAttribute(TIMEOUT);
        String earlyConnect = element.getAttribute(EARLY_CONNECT);

        if (!isBlank(port)) {
            applicationContext.setPort(Integer.parseInt(port));
        }
        if (!isBlank(name)) {
            applicationContext.setApplicationName(name);
        }
        if (!isBlank(timeout)) {
            applicationContext.setWaitResponseTimeout(Long.parseLong(timeout));
        }

        if (!isBlank(earlyConnect)) {
            applicationContext.setEarlyConnect((Boolean.parseBoolean(earlyConnect)));
        }
    }
}

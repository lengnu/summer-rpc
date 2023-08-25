package com.duwei.summer.rpc.property;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 18:59
 * @since: 1.0
 */
public class AttributeAccessorSupport implements AttributeAccessor{
    private final Map<String,Object> attributes = new LinkedHashMap<>();
    @Override
    public void setAttribute(String name, Object value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        attributes.put(name,value);
    }

    @Override
    public Object getAttribute(String name) {
        Objects.requireNonNull(name);
        return attributes.get(name);
    }
}

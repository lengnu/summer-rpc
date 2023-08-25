package com.duwei.summer.rpc.property;

/**
 * <p>
 *  属性访问器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 18:58
 * @since: 1.0
 */
public interface AttributeAccessor
{
    void setAttribute(String name,Object value);

    Object getAttribute(String  name);
}

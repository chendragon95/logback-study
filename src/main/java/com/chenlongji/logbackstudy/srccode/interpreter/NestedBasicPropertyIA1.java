/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 *//*

package com.chenlongji.logbackstudy.srccode.interpreter;

import ch.qos.logback.core.joran.action.IADataForBasicProperty;
import ch.qos.logback.core.joran.action.ImplicitAction;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.util.AggregationType;
import org.xml.sax.Attributes;

import java.util.Stack;

*/
/**
 * This action is responsible for tying together a parent object with one of its
 * <em>simple</em> properties specified as an element but for which there is
 * no explicit rule.
 *
 * @author Ceki G&uuml;lc&uuml;
 *//*

public class NestedBasicPropertyIA1 extends ImplicitAction {
    // 记录action的解析时需要使用到数据的栈.
    //   其中IADataForBasicProperty包含当前action要解析的属性 的类型、名称、父类的bean构建的PropertySetter
    Stack<IADataForBasicProperty> actionDataStack = new Stack<IADataForBasicProperty>();
    // bean描述器的缓存器
    private final BeanDescriptionCache beanDescriptionCache;

    public NestedBasicPropertyIA1(BeanDescriptionCache beanDescriptionCache) {
        this.beanDescriptionCache = beanDescriptionCache;
    }

    */
/**
     * 判断NestedBasicPropertyIA是否适用
     *//*

    @Override
    public boolean isApplicable(ElementPath elementPath, Attributes attributes, InterpretationContext ec) {
        // 获取当前标签的名字
        String nestedElementTagName = elementPath.peekLast();
        if (ec.isEmpty()) {
            return false;
        }

        // 获取当前action要解析的属性 的父类bean, 构建PropertySetter对象, 用于判断属性参数类型和反射赋值
        Object o = ec.peekObject();
        PropertySetter parentBean = new PropertySetter(beanDescriptionCache, o);
        parentBean.setContext(context);

        // 获取当前action要解析的属性的类型 (不展开)
        AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);

        // NestedBasicPropertyIA仅支持处理 属性为简单类型的 或 静态set和add方法属性
        //   这里的简单类型包含八大基本类型, Void, Enum, Charset, java.lang包下对象等
        switch (aggregationType) {
        case NOT_FOUND:
        case AS_COMPLEX_PROPERTY:
        case AS_COMPLEX_PROPERTY_COLLECTION:
            return false;

        case AS_BASIC_PROPERTY:
        case AS_BASIC_PROPERTY_COLLECTION:
            // 构建IADataForBasicProperty, 缓存到actionDataStack栈中, 供NestedBasicPropertyIA此Action执行start、body、end方法时使用
            IADataForBasicProperty ad = new IADataForBasicProperty(parentBean, aggregationType, nestedElementTagName);
            actionDataStack.push(ad);
            return true;
        default:
            addError("PropertySetter.canContainComponent returned " + aggregationType);
            return false;
        }
    }

    @Override
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        // NOP
    }

    public void body(InterpretationContext ec, String body) {
        // 获取具体的属性值和actionData
        String finalBody = ec.subst(body);
        IADataForBasicProperty actionData = (IADataForBasicProperty) actionDataStack.peek();
        switch (actionData.aggregationType) {
        case AS_BASIC_PROPERTY:
            // 根本进不来这个方法
            actionData.parentBean.setProperty(actionData.propertyName, finalBody);
            break;
        case AS_BASIC_PROPERTY_COLLECTION:
            // 只会执行该case. 反射的方式设置对象的属性值
            actionData.parentBean.addBasicProperty(actionData.propertyName, finalBody);
            break;
        default:
            addError("Unexpected aggregationType " + actionData.aggregationType);
        }
    }

    public void end(InterpretationContext ec, String tagName) {
        // 处理结束, 弹出缓存的action内的IADataForBasicProperty数据
        actionDataStack.pop();
    }
}
*/

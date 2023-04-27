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
 */
package com.chenlongji.logbackstudy.srccode.interpreter;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.InPlayListener;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Locator;

import java.util.*;

public class InterpretationContext1 extends ContextAwareBase implements PropertyContainer {
    // 对象栈, 存储记录解析过程中标签解析出来的对象, 完成该对象所有属性赋值则出栈
    Stack<Object> objectStack;
    // 记录临时的标签对象. 例如先解析出Appender, 则会暂存在map中, 键值对为 APPENDER_BAG -> {console=consoleAppender对象, ...}
    Map<String, Object> objectMap;
    // 记录解析出来的<property>标签的属性. 示例: log.path -> /logs/logback-study
    Map<String, String> propertiesMap;
    // 解析器对象
    Interpreter joranInterpreter;
    // 监听器列表, 用于执行inPlayf方法, 一般该方法都是先寄存envent事件待后面处理
    final List<InPlayListener> listenerList = new ArrayList<InPlayListener>();
    // 默认嵌套组件注册表. 指定嵌套组件如AppenderBase的layout, encoder以及SSl等属性的默认实现
    DefaultNestedComponentRegistry defaultNestedComponentRegistry = new DefaultNestedComponentRegistry();

    public InterpretationContext1(Context context, Interpreter joranInterpreter) {
        this.context = context;
        this.joranInterpreter = joranInterpreter;
        objectStack = new Stack<Object>();
        objectMap = new HashMap<String, Object>(5);
        propertiesMap = new HashMap<String, String>(5);
    }

    public DefaultNestedComponentRegistry getDefaultNestedComponentRegistry() {
        return defaultNestedComponentRegistry;
    }

    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap<String, String>(propertiesMap);
    }

    void setPropertiesMap(Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    String updateLocationInfo(String msg) {
        Locator locator = joranInterpreter.getLocator();

        if (locator != null) {
            return msg + locator.getLineNumber() + ":" + locator.getColumnNumber();
        } else {
            return msg;
        }
    }

    public Locator getLocator() {
        return joranInterpreter.getLocator();
    }

    public Interpreter getJoranInterpreter() {
        return joranInterpreter;
    }

    public Stack<Object> getObjectStack() {
        return objectStack;
    }

    public boolean isEmpty() {
        return objectStack.isEmpty();
    }

    public Object peekObject() {
        return objectStack.peek();
    }

    public void pushObject(Object o) {
        objectStack.push(o);
    }

    public Object popObject() {
        return objectStack.pop();
    }

    public Object getObject(int i) {
        return objectStack.get(i);
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    /**
     * 添加property属性, 存在则覆盖
     */
    public void addSubstitutionProperty(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        value = value.trim();
        propertiesMap.put(key, value);
    }

    /**
     * 批量添加property属性
     */
    public void addSubstitutionProperties(Properties props) {
        if (props == null) {
            return;
        }
        for (Object keyObject : props.keySet()) {
            String key = (String) keyObject;
            String val = props.getProperty(key);
            addSubstitutionProperty(key, val);
        }
    }

    /**
     * 获取property属性值
     */
    public String getProperty(String key) {
        String v = propertiesMap.get(key);
        if (v != null) {
            return v;
        } else {
            return context.getProperty(key);
        }
    }

    /**
     * 从上下文, propertiesMap等中获取属性值
     * 可以解析{}内的内容出来
     */
    public String subst(String value) {
        if (value == null) {
            return null;
        }
        return OptionHelper.substVars(value, this, context);
    }

    public boolean isListenerListEmpty() {
        return listenerList.isEmpty();
    }

    public void addInPlayListener(InPlayListener ipl) {
        if (listenerList.contains(ipl)) {
            addWarn("InPlayListener " + ipl + " has been already registered");
        } else {
            listenerList.add(ipl);
        }
    }

    public boolean removeInPlayListener(InPlayListener ipl) {
        return listenerList.remove(ipl);
    }

    void fireInPlay(SaxEvent event) {
        for (InPlayListener ipl : listenerList) {
            ipl.inPlay(event);
        }
    }

}

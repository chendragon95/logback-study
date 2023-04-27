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
package com.chenlongji.logbackstudy.srccode.layout;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PostCompileProcessor;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;

import java.util.HashMap;
import java.util.Map;

abstract public class PatternLayoutBase1<E> extends LayoutBase<E> {

    static final int INTIAL_STRING_BUILDER_SIZE = 256;
    // 转换器执行链. 用于日志输出时拼接出最终的日志内容
    Converter<E> head;
    // xml配置的日志模式
    String pattern;
    protected PostCompileProcessor<E> postCompileProcessor;
    
    Map<String, String> instanceConverterMap = new HashMap<String, String>();
    protected boolean outputPatternAsHeader = false;
    
    /**
     * Concrete implementations of this class are responsible for elaborating the
     * mapping between pattern words and converters.
     * 
     * @return A map associating pattern words to the names of converter classes
     */
    abstract public Map<String, String> getDefaultConverterMap();

    /**
     * Returns a map where the default converter map is merged with the map
     * contained in the context.
     */
    public Map<String, String> getEffectiveConverterMap() {
        Map<String, String> effectiveMap = new HashMap<String, String>();

        // 添加默认的转换器实现映射 (主要来源)
        Map<String, String> defaultMap = getDefaultConverterMap();
        if (defaultMap != null) {
            effectiveMap.putAll(defaultMap);
        }

        // 添加初始化过程中添加到LoggerContext的objectMap中key为PATTERN_RULE_REGISTRY的转换器实现映射
        Context context = getContext();
        if (context != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> contextMap = (Map<String, String>) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
            if (contextMap != null) {
                effectiveMap.putAll(contextMap);
            }
        }
        // 添加实例的转换器实现映射. 例如使用了SyslogAppender, 就会添加syslogStart -> ch.qos.logback.classic.pattern.SyslogStartConverter
        effectiveMap.putAll(instanceConverterMap);
        return effectiveMap;
    }

    public void start() {
        if (pattern == null || pattern.length() == 0) {
            addError("Empty or null pattern.");
            return;
        }
        try {
            // 由于此文篇幅太长, Parser类的代码不展开
            // 初始化布局解析器, 使用状态机模式完成Parser的tokenList初始化(将pattern解析成一个个token对象). 不展开
            Parser<E> p = new Parser<E>(pattern);
            if (getContext() != null) {
                p.setContext(getContext());
            }
            // 将Parser的tokenList解析成Node链表. 不展开
            Node t = p.parse();
            // 核心代码: 获取有效的关键字映射的转换器全限定类名, 使用Node链表 解析出 转换器链. compile方法不展开
            this.head = p.compile(t, getEffectiveConverterMap());
            if (postCompileProcessor != null) {
                // 没有异常转换器则在转换器链尾添加一个
                postCompileProcessor.process(context, head);
            }
            // 设置所有转换器上下文属性
            ConverterUtil.setContextForConverters(getContext(), head);
            // 设置所有转换器start属性为true
            ConverterUtil.startConverters(this.head);
            super.start();
        } catch (ScanException sce) {
            StatusManager sm = getContext().getStatusManager();
            sm.add(new ErrorStatus("Failed to parse pattern \"" + getPattern() + "\".", this, sce));
        }
    }

    public void setPostCompileProcessor(PostCompileProcessor<E> postCompileProcessor) {
        this.postCompileProcessor = postCompileProcessor;
    }

    /**
     *
     * @param head
     * @deprecated  Use {@link ConverterUtil#setContextForConverters} instead. This method will
     *  be removed in future releases.
     */
    protected void setContextForConverters(Converter<E> head) {
        ConverterUtil.setContextForConverters(getContext(), head);
    }

    protected String writeLoopOnConverters(E event) {
        StringBuilder strBuilder = new StringBuilder(INTIAL_STRING_BUILDER_SIZE);
        // 遍历转换器执行链解析日志内容
        Converter<E> c = head;
        while (c != null) {
            // 使用该转换器 获取该转换器 转换出来的部分内容, 加入strBuilder中
            c.write(strBuilder, event);
            c = c.getNext();
        }
        return strBuilder.toString();
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String toString() {
        return this.getClass().getName() + "(\"" + getPattern() + "\")";
    }

    public Map<String, String> getInstanceConverterMap() {
        return instanceConverterMap;
    }

    protected String getPresentationHeaderPrefix() {
        return CoreConstants.EMPTY_STRING;
    }

    public boolean isOutputPatternAsHeader() {
        return outputPatternAsHeader;
    }

    public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
        this.outputPatternAsHeader = outputPatternAsHeader;
    }

    @Override
    public String getPresentationHeader() {
        if (outputPatternAsHeader)
            return getPresentationHeaderPrefix() + pattern;
        else
            return super.getPresentationHeader();
    }
}

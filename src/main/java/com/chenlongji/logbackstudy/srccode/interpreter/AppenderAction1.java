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

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionConst;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppenderAction1<E> extends Action {
    Appender<E> appender;
    private boolean inError = false;

    @Override
    public void begin(InterpretationContext ec, String localName, Attributes attributes) throws ActionException {
        appender = null;
        inError = false;
        // 获取<appender>的class属性
        String className = attributes.getValue(CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(className)) {
            inError = true;
            return;
        }

        try {
            // 反射创建Appender对象
            appender = (Appender<E>) OptionHelper.instantiateByClassName(className, Appender.class, context);
            appender.setContext(context);

            // 获取<appender>的name属性并设置
            String appenderName = ec.subst(attributes.getValue(NAME_ATTRIBUTE));
            if (OptionHelper.isEmpty(appenderName)) {
            } else {
                appender.setName(appenderName);
            }

            // 将appender寄存到 InterpretationContext的objectMap中. 键值对为 APPENDER_BAG -> {console=consoleAppender对象, ...}
            HashMap<String, Appender<E>> appenderBag = (HashMap<String, Appender<E>>) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
            appenderBag.put(appenderName, appender);
            // 同时将appender对象压入objectStack栈中待后续处理内嵌标签时使用
            ec.pushObject(appender);
        } catch (Exception oops) {
            inError = true;
            throw new ActionException(oops);
        }
    }

    /**
     * 子元素都被解析之后，现在激活appender其他属性
     */
    @Override
    public void end(InterpretationContext ec, String name) {
        if (inError) {
            return;
        }
        if (appender instanceof LifeCycle) {
            // 执行appender的start方法, 不展开
            ((LifeCycle) appender).start();
        }

        // 当前操作的appender和objectStack栈顶一致时, 弹出
        Object o = ec.peekObject();
        if (o != appender) {
        } else {
            ec.popObject();
        }
    }
}

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

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class ContextNameAction1 extends Action {
    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
    }

    @Override
    public void body(InterpretationContext ec, String body) {
        // 解析出消息体内的内容. subst方法可以解析出{}内的值
        String finalBody = ec.subst(body);
        try {
            // 设置LoggerContext的名字
            context.setName(finalBody);
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public void end(InterpretationContext ec, String name) {
    }
}

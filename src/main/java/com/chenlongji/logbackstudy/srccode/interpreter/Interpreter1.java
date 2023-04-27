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

package com.chenlongji.logbackstudy.srccode;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ImplicitAction;
import ch.qos.logback.core.joran.event.BodyEvent;
import ch.qos.logback.core.joran.event.EndEvent;
import ch.qos.logback.core.joran.event.StartEvent;
import ch.qos.logback.core.joran.spi.*;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import java.util.*;

public class Interpreter1 {
    private static List<Action> EMPTY_LIST = new Vector<Action>(0);
    // 实例规则仓库. 示例: [configuration][appender] -> AppenderAction
    final private RuleStore ruleStore;
    // 解析器上下文, 存储解析过程重要的信息
    final private InterpretationContext interpretationContext;
    // 隐晦的规则列表, 用于处理ruleStore不包含的嵌套属性解析初始化和绑定等
    final private ArrayList<ImplicitAction> implicitActions;
    // 保留定位器信息的记录类 (不重要)
    final private CAI_WithLocatorSupport cai;
    // 当前解析标签的路径, 例如 [configuration] [appender]. 解析过程中类似栈的使用
    private ElementPath elementPath;
    // 当前解析位于xml文件的位置
    Locator locator;
    // 事件解析执行器, 用于解析SaxEvent事件
    EventPlayer eventPlayer;
    // action列表的栈, 用于记录解析过程中的解析规则
    Stack<List<Action>> actionListStack;

    // skip指定的标签, 它的所有嵌套元素都会被跳过
    ElementPath skip = null;

    public Interpreter(Context context, RuleStore rs, ElementPath initialElementPath) {
        this.cai = new CAI_WithLocatorSupport(context, this);
        ruleStore = rs;
        interpretationContext = new InterpretationContext(context, this);
        implicitActions = new ArrayList<ImplicitAction>(3);
        this.elementPath = initialElementPath;
        actionListStack = new Stack<List<Action>>();
        eventPlayer = new EventPlayer(this);
    }

    public EventPlayer getEventPlayer() {
        return eventPlayer;
    }

    public void setInterpretationContextPropertiesMap(Map<String, String> propertiesMap) {
        interpretationContext.setPropertiesMap(propertiesMap);
    }

    */
/**
     * @deprecated replaced by {@link #getInterpretationContext()}
     *//*

    public InterpretationContext getExecutionContext() {
        return getInterpretationContext();
    }

    public InterpretationContext getInterpretationContext() {
        return interpretationContext;
    }

    public void startDocument() {
    }

    */
/**
     * 添加空的action列表占位 (入栈占位)
     *//*

    private void pushEmptyActionList() {
        actionListStack.add(EMPTY_LIST);
    }

    */
/**
     * 解析标签头(入口)
     *//*

    public void startElement(StartEvent se) {
        // 记录当前解析 位于xml文件的位置
        setDocumentLocator(se.getLocator());
        startElement(se.namespaceURI, se.localName, se.qName, se.attributes);
    }

    */
/**
     * 解析标签头
     *//*

    private void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        // localName有值取localName, 没值取qName. 拓展: 示例<a bb:ccc="123"/> 其中cc就是本地名称localName, bb:ccc就是限定名称qName
        String tagName = getTagName(localName, qName);
        // 使用elementPath, 类似栈的形式记录当前解析标签的层级. 例如解析到<configuration>内的<appender>标签, 则elementPath伪栈中有configuration和appender
        elementPath.push(tagName);

        // 解析标签头异常时使用skip跳过 异常解析的标签的内嵌标签
        if (skip != null) {
            // 添加空的action列表占位 (入栈占位)
            pushEmptyActionList();
            return;
        }

        // 重要代码: 获取适用的Action列表, 一般都是返回一个元素. 先从ruleStore找常规的Action, 找不到再到implicitActions中找
        List<Action> applicableActionList = getApplicableActionList(elementPath, atts);
        if (applicableActionList != null) {
            // action事件列表进栈, 以便处理body和end事件时使用
            actionListStack.add(applicableActionList);
            // 核心代码: 执行action列表的begin事件
            callBeginAction(applicableActionList, tagName, atts);
        } else {
            // 添加空的action列表占位 (入栈占位)
            pushEmptyActionList();
            String errMsg = "no applicable action for [" + tagName + "], current ElementPath  is [" + elementPath + "]";
            cai.addError(errMsg);
        }
    }

    */
/**
     * 执行action列表的begin事件
     *//*

    void callBeginAction(List<Action> applicableActionList, String tagName, Attributes atts) {
        if (applicableActionList == null) {
            return;
        }

        // 遍历执行action的begin事件, 一般一个标签都是只有一个Action的. 这里源码仅看AppenderAction
        Iterator<Action> i = applicableActionList.iterator();
        while (i.hasNext()) {
            Action action = (Action) i.next();
            try {
                // 核心代码
                action.begin(interpretationContext, tagName, atts);
            } catch (ActionException e) {
                // 解析标签开始时就异常则记录当前标签路径, 防止其内嵌的标签继续解析
                skip = elementPath.duplicate();
                cai.addError("ActionException in Action for tag [" + tagName + "]", e);
            } catch (RuntimeException e) {
                // 解析标签开始时就异常则记录当前标签路径, 防止其内嵌的标签继续解析
                skip = elementPath.duplicate();
                cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
            }
        }
    }



    */
/**
     * 解析标签体 (标签体就是 字符串)
     *//*

    public void characters(BodyEvent be) {
        // 记录当前解析 位于xml文件的位置
        setDocumentLocator(be.locator);

        String body = be.getText();
        // 读出startElement时加入的栈顶Action列表. 注意, 这里仅读出
        List<Action> applicableActionList = actionListStack.peek();

        if (body != null) {
            body = body.trim();
            if (body.length() > 0) {
                // 核心代码: 执行action列表的body事件
                callBodyAction(applicableActionList, body);
            }
        }
    }

    */
/**
     * 核心代码: 执行action列表的body事件
     *//*

    private void callBodyAction(List<Action> applicableActionList, String body) {
        if (applicableActionList == null) {
            return;
        }
        // 遍历执行action的body事件, 一般一个标签都是只有一个Action的. 这里源码仅看ContextNameAction
        Iterator<Action> i = applicableActionList.iterator();
        while (i.hasNext()) {
            Action action = i.next();
            try {
                // 核心代码
                action.body(interpretationContext, body);
            } catch (ActionException ae) {
                cai.addError("Exception in end() methd for action [" + action + "]", ae);
            }
        }
    }



    */
/**
     * 解析标签尾(入口)
     *//*

    public void endElement(EndEvent endEvent) {
        // 记录当前解析 位于xml文件的位置
        setDocumentLocator(endEvent.locator);
        endElement(endEvent.namespaceURI, endEvent.localName, endEvent.qName);
    }

    */
/**
     * 解析标签尾
     *//*

    private void endElement(String namespaceURI, String localName, String qName) {
        // 弹出startElement时加入的栈顶Action列表
        List<Action> applicableActionList = (List<Action>) actionListStack.pop();

        // 若当前标签路径和skip中记录的解析异常标签头路径一致时, 则表示该标签闭环了(内嵌都遍历完了), 设置skip为空
        if (skip != null) {
            if (skip.equals(elementPath)) {
                skip = null;
            }
        } else if (applicableActionList != EMPTY_LIST) {
            // 核心代码: 执行action列表的end事件
            callEndAction(applicableActionList, getTagName(localName, qName));
        }

        // 当前解析的标签路径出栈 (父层级还是保留的)
        elementPath.pop();
    }

    */
/**
     * 执行action列表的end事件
     *//*

    private void callEndAction(List<Action> applicableActionList, String tagName) {
        if (applicableActionList == null) {
            return;
        }

        // 遍历执行action的end事件, 一般一个标签都是只有一个Action的. 这里源码仅看AppenderAction
        Iterator<Action> i = applicableActionList.iterator();
        while (i.hasNext()) {
            Action action = i.next();
            try {
                // 核心代码
                action.end(interpretationContext, tagName);
            } catch (ActionException ae) {
                cai.addError("ActionException in Action for tag [" + tagName + "]", ae);
            } catch (RuntimeException e) {
                cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
            }
        }
    }









    */
/**
     * 到implicitActions中查找符合的Action. 有返回时仅返回一个元素
     *//*

    List<Action> lookupImplicitAction(ElementPath elementPath, Attributes attributes, InterpretationContext ec) {
        int len = implicitActions.size();
        // 遍历列表, 其实列表就只有NestedComplexPropertyIA和NestedBasicPropertyIA
        for (int i = 0; i < len; i++) {
            ImplicitAction ia = (ImplicitAction) implicitActions.get(i);
            // 若属性为简单类型的则返回NestedBasicPropertyIA, 反之返回NestedComplexPropertyIA
            // 这里的简单类型包含八大基本类型, Void, Sting, Enum, Charset等
            if (ia.isApplicable(elementPath, attributes, ec)) {
                List<Action> actionList = new ArrayList<Action>(1);
                actionList.add(ia);
                return actionList;
            }
        }
        return null;
    }

    */
/**
     * 获取适用的Action列表
     *//*

    List<Action> getApplicableActionList(ElementPath elementPath, Attributes attributes) {
        // 通过当前解析标签的路径, 从ruleStore中获取匹配的Action列表
        List<Action> applicableActionList = ruleStore.matchActions(elementPath);

        // ruleStore找不到, 则到implicitActions中查找, 根据上级标签映射的对象和当前标签的名字查找. 这里返回的列表只有一个元素
        if (applicableActionList == null) {
            applicableActionList = lookupImplicitAction(elementPath, attributes, interpretationContext);
        }

        return applicableActionList;
    }











    public Locator getLocator() {
        return locator;
    }

    public void setDocumentLocator(Locator l) {
        locator = l;
    }

    String getTagName(String localName, String qName) {
        String tagName = localName;
        if ((tagName == null) || (tagName.length() < 1)) {
            tagName = qName;
        }
        return tagName;
    }

    public void addImplicitAction(ImplicitAction ia) {
        implicitActions.add(ia);
    }

    public RuleStore getRuleStore() {
        return ruleStore;
    }
}

*/

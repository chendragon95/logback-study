/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chenlongji.logbackstudy.srccode.springboot;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.InPlayListener;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.util.OptionHelper;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Logback {@link Action} to support {@code <springProfile>} tags. Allows section of a
 * logback configuration to only be enabled when a specific profile is active.
 *
 * @author Phillip Webb
 * @author Eddú Meléndez
 */
class SpringProfileAction1 extends Action implements InPlayListener {
	// 环境信息
	private final Environment environment;
	// 深度. 用于拦截嵌套的<springProfile>错误标签
	private int depth = 0;
	// 记录当前的<springProfile>是否满足运行的环境(即springProfile的name是否包含 项目的spring.profiles.active)
	private boolean acceptsProfile;
	// 事件暂存区. 寄存<springProfile>及内嵌标签事件的 缓存事件列表
	private List<SaxEvent> events;

	SpringProfileAction1(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
		// 深度加一, 标志开始解析<springProfile>
		this.depth++;
		// 只能处理外层的<springProfile>, 嵌套直接结束(示例: <springProfile><springProfile>...<springProfile/><springProfile/>)
		if (this.depth != 1) {
			return;
		}
		// 当前action入栈, 待后续使用
		ic.pushObject(this);
		// 记录 当前的<springProfile>是否满足运行的环境情况
		this.acceptsProfile = acceptsProfiles(ic, attributes);
		this.events = new ArrayList<>();
		// 将当前的SpringProfileAction加入InterpretationContext的监听器列表中listenerList. 该监听器用于添加寄存的内嵌标签事件. 不展开
		ic.addInPlayListener(this);
	}

	private boolean acceptsProfiles(InterpretationContext ic, Attributes attributes) {
		if (this.environment == null) {
			return false;
		}
		// 获取<springProfile>属性name, 按","分隔. 不展开
		String[] profileNames = StringUtils
			.trimArrayElements(StringUtils.commaDelimitedListToStringArray(attributes.getValue(NAME_ATTRIBUTE)));
		if (profileNames.length == 0) {
			return false;
		}
		// 获取profileNames的真实值. 不展开
		for (int i = 0; i < profileNames.length; i++) {
			profileNames[i] = OptionHelper.substVars(profileNames[i], ic, this.context);
		}
		// 判断当前的<springProfile>是否满足运行的环境. 不展开
		return this.environment.acceptsProfiles(Profiles.of(profileNames));
	}

	@Override
	public void end(InterpretationContext ic, String name) throws ActionException {
		// 深度减一, 标志结束解析<springProfile>
		this.depth--;
		// 嵌套<springProfile>标签, 直接结束
		if (this.depth != 0) {
			return;
		}
		// 移除掉SpringProfileAction这个监听器
		ic.removeInPlayListener(this);
		// 校验当前action是否为栈顶对象, 是则弹出
		verifyAndPop(ic);
		// 重要代码: 当前<springProfile>有效, 将<springProfile>的内嵌标签事件 丢回正在处理eventList列表中. 插入的位置为下一个执行事件前面
		if (this.acceptsProfile) {
			addEventsToPlayer(ic);
		}
	}

	private void verifyAndPop(InterpretationContext ic) {
		// 校验当前action是否为栈顶对象, 是则弹出. 不展开
		Object o = ic.peekObject();
		Assert.state(o != null, "Unexpected null object on stack");
		Assert.isInstanceOf(SpringProfileAction1.class, o, "logback stack error");
		Assert.state(o == this, "ProfileAction different than current one on stack");
		ic.popObject();
	}

	private void addEventsToPlayer(InterpretationContext ic) {
		// 移除<springProfile>和<springProfile/>事件
		Interpreter interpreter = ic.getJoranInterpreter();
		this.events.remove(0);
		this.events.remove(this.events.size() - 1);
		// 将<springProfile>内嵌事件丢回正在处理eventList列表中. 插入的位置为下一个执行事件前面
		interpreter.getEventPlayer().addEventsDynamically(this.events, 1);
	}

	@Override
	public void inPlay(SaxEvent event) {
		// 使用SpringProfileAction该监听器添加事件到事件寄存器events中.
		// 调用处: 在EventPlayer的play方法中调用, 将当前的事件添加到各个监听器中去. 不展开
		this.events.add(event);
	}
}

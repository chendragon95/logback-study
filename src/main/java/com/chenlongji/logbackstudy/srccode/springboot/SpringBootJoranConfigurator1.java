//
//package com.chenlongji.logbackstudy.srccode.springboot;
//
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.joran.action.NOPAction;
//import ch.qos.logback.core.joran.spi.ElementSelector;
//import ch.qos.logback.core.joran.spi.RuleStore;
//import org.springframework.boot.logging.LoggingInitializationContext;
//import org.springframework.boot.logging.logback.SpringProfileAction;
//import org.springframework.boot.logging.logback.SpringPropertyAction;
//import org.springframework.core.env.Environment;
//
///**
// * Extended version of the Logback {@link JoranConfigurator} that adds additional Spring
// * Boot rules.
// *
// * @author Phillip Webb
// */
//class SpringBootJoranConfigurator1 extends JoranConfigurator {
//	// springboot的logger初始化上下文对象, 存储了environment环境信息
//	private LoggingInitializationContext initializationContext;
//
//	SpringBootJoranConfigurator1(LoggingInitializationContext initializationContext) {
//		this.initializationContext = initializationContext;
//	}
//
//	/**
//	 * 添加实例规则
//	 */
//	@Override
//	public void addInstanceRules(RuleStore rs) {
//		super.addInstanceRules(rs);
//		Environment environment = this.initializationContext.getEnvironment();
//		// 添加解析<configuration>下<springProperty>的Action
//		rs.addRule(new ElementSelector("configuration/springProperty"), new SpringPropertyAction(environment));
//		// 添加解析* <springProfile>的Action
//		rs.addRule(new ElementSelector("*/springProfile"), new SpringProfileAction(environment));
//		// 添加* <springProfile>下的Action. 该Action为无操作Action, 由SpringProfileAction去处理<springProfile>下的标签内容
//		rs.addRule(new ElementSelector("*/springProfile/*"), new NOPAction());
//	}
//}

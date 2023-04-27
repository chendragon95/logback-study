package com.chenlongji.logbackstudy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author clj
 * LoggerFactory获取logger对象
 * 使用文档: https://logback.qos.ch/manual/
 */
public class Test1 {
    /**
     * logback使用需要依赖
     *    dependency>
     *        <groupId>org.slf4j</groupId>
     *        <artifactId>slf4j-api</artifactId>
     *        <version>1.7.5</version>
     *    </dependency>
     *    <dependency>
     *        <groupId>ch.qos.logback</groupId>
     *        <artifactId>logback-core</artifactId>
     *        <version>1.0.11</version>
     *    </dependency>
     *    <dependency>
     *        <groupId>ch.qos.logback</groupId>
     *        <artifactId>logback-classic</artifactId>
     *        <version>1.0.11</version>
     *    </dependency>
     *  其中 logback-classic会自动导入依赖slf4j-api 和 logback-core, 所以maven项目只需要导出logback-classic即可
     *      注: slf4j-api不是logback的一部分, 是另一个项目
     *  对于springboot项目, 依赖spring-boot-starter(或其父类spring-boot-starter-web) 会自动导入依赖logback-classic, 故不需要额外导入logback的依赖
     *  补充网上一个说法:
     *     这里再补充一个问题，slf4j-api 中不包含 StaticLoggerBinder 类，为什么能编译通过呢？
     *     其实我们项目中用到的 slf4j-api 是已经编译好的 class 文件，所以不需要再次编译。
     *     但是，编译前 slf4j-api 中是包含 StaticLoggerBinder.java 的，
     *     且编译后也存在 StaticLoggerBinder1.class ，只是这个文件被手动删除了
     *
     *   补充: spring项目的logback-spring.xml生效是因为
     *      org.springframework.boot.context.logging.LoggingApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     *      org.springframework.boot.logging.AbstractLoggingSystem#getSpringConfigLocations()
     *
     *      spring先按照日志门面模式(这里使用的是logback)创建出loggerContext对象
     *      在spring初始化时, 读取logback-spring.xml文件, 将loggerContext对象信息重置, 重置代码位置 org.springframework.boot.logging.logback.LogbackLoggingSystem#configureByResourceUrl(org.springframework.boot.logging.LoggingInitializationContext, ch.qos.logback.classic.LoggerContext, java.net.URL)
     *          核心代码:
     *             JoranConfigurator configurator = new SpringBootJoranConfigurator(initializationContext);
     *             configurator.setContext(loggerContext);
     *             configurator.doConfigure(url);
     *      springboot的日志配置类: SpringBootJoranConfigurator
     *
     *    模式转换器: ch.qos.logback.classic.PatternLayout
     */
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(Test1.class);
        logger.info("我是info级别的日志");

        /*for (int i = 0; i < 100; i++) {
            logger.info("我是info级别的日志");
            Thread.sleep(20000);
        }*/

        //s(logger);
        //soLongMethodNameIsMy(logger);
    }

    private static void s(Logger logger) {
        // [%2.-5method]
        // [%-2.5method]
        logger.info("超短方法s");
    }

    private static void soLongMethodNameIsMy(Logger logger) {
        logger.info("超长的方法名的方法");
    }

}

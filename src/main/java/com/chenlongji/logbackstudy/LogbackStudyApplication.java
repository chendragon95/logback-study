package com.chenlongji.logbackstudy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author clj
 * springboot项目会使用到logback-spring.xml作为logback的配置文件
 */
@SpringBootApplication
public class LogbackStudyApplication {

    public static void main(String[] args) {
        // org.springframework.boot.context.logging.LoggingApplicationListener使spring初始化时就完成了logback读取配置和初始化
        ConfigurableApplicationContext context = SpringApplication.run(LogbackStudyApplication.class, args);

        Logger logger = LoggerFactory.getLogger(Test1.class);
        logger.info("我是info级别的日志");

        context.close();
    }

}

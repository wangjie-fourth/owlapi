package com.jena.demo;

import com.jena.demo.global.GlobalOwlContent;
import com.jena.demo.service.OwlContentUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SpringBootApplication
public class DemoApplication {

    public static final GlobalOwlContent globalOwlContent = new GlobalOwlContent();

    public static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    public static final Lock read = rwl.readLock();
    public static final Lock write = rwl.writeLock();

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DemoApplication.class, args);
        // 初始化缓存对象
        OwlContentUtil bean = run.getBean(OwlContentUtil.class);
        bean.initGlobalOwlContent();
    }
}

package com.hss.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IDGeneratorTest {

    private static final Logger logger = LoggerFactory.getLogger(IDGeneratorTest.class);

    @Autowired
    private IDGenerator idGenerator;

    @Test
    public void nextId() {
        String busKey = "cn.hss.demo";
        for (int i = 0; i < 1000; i++) {
            logger.info("genId={}",idGenerator.nextId(busKey));
        }
    }
}
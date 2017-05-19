package com.ericsson.ei.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RulesHandlerTest {

    RulesHandler classUnderTest = new RulesHandler();
//
//    static Logger log = (Logger) LoggerFactory.getLogger(RulesHandlerTest.class);

    @Test
    public void testPrintJson() {
        System.out.print("TEST!!!");
//        System.out.print(classUnderTest.PrintJson());
//
//        String testJson = "This is only for debug!";
//        assertEquals(testJson, classUnderTest.PrintJson());
    }

}

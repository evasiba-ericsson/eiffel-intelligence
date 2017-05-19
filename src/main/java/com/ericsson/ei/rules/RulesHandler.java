package com.ericsson.ei.rules;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.*;


//Logger?

@Configuration
@Component
@PropertySource("classpath:application.properties")
public class RulesHandler {
    private static Logger log = LoggerFactory.getLogger(RulesHandler.class);

    @Value("${rules}") private String jsonFilePath;
    @Autowired
    private Environment env;

    private static String jsonFileContent;
    private static GsonJsonParser parser;
    static List<Object> parsedJason;

    public RulesHandler() {
        super();
        try {
            String prop =  env.getProperty("rules");
            System.out.print("JSON: " + jsonFilePath);
            //FileUtils.readFileToString(new File(jsonFilePath));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        parser = new GsonJsonParser();
//        parsedJason = parser.parseList(jsonFileContent);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public String PrintJson() {
        System.out.print(parsedJason);
        log.info("TEST!");
        return parsedJason.toString();
    }

    public Object getRulesForEvent(/*event*/) {
        for (Object jsonObj : parsedJason) {
            if (true /*match with event*/) {
                return jsonObj;
            }
        }
        return null;
    }


}

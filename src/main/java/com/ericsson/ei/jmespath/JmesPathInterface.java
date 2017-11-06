package com.ericsson.ei.jmespath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.function.FunctionRegistry;
import io.burt.jmespath.jackson.JacksonRuntime;

@Component
public class JmesPathInterface {

    static Logger log = (Logger) LoggerFactory.getLogger(JmesPathInterface.class);

    private JmesPath<JsonNode> jmespath;

    public JmesPathInterface() {
        FunctionRegistry defaultFunctions = FunctionRegistry.defaultRegistry();
        FunctionRegistry customFunctions = defaultFunctions.extend(new DiffFunction());
        jmespath = new JacksonRuntime(customFunctions);
    }


    public JsonNode runRuleOnEvent(String rule, String input) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode result = null;
        try {
            result = objectMapper.readValue("{}", JsonNode.class);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }

        if (rule != null && !rule.isEmpty()) {
            JsonNode event = null;
            if (input == null)
                input = "";
            Expression<JsonNode> expression = jmespath.compile(rule);
            try {
                event = objectMapper.readValue(input, JsonNode.class);
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
            result = expression.search(event);
        }

        return result;
    }
}

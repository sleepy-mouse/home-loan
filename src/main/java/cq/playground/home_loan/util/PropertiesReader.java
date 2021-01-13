package cq.playground.home_loan.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class PropertiesReader {
    private static final Map<String, String> EMPTY = Map.of();
    private static final Map<String, Boolean> MANDATORY_PROPERTIES = Map.of(
            "endpoint", false,
            "region", true
    );
    private Map<String, String> config = EMPTY;

    @SuppressWarnings("unchecked")
    public Map<String, String> read() {
        if (config.isEmpty()) {
            var resource = getClass().getClassLoader().getResource("config.properties");
            if (resource == null) {
                log.error("Config file cannot be found.");
                return EMPTY;
            }
            try (var reader = new BufferedReader(new FileReader(resource.getFile()))) {
                var props = new Properties();
                props.load(reader);
                config = new HashMap<String, String>((Map) props);
                validate(config);
            } catch (Exception e) {
                config = EMPTY;
                throw new RuntimeException(e);
            }
        }
        return config;
    }

    private void validate(Map<String, String> config) {
        for (String key : MANDATORY_PROPERTIES.keySet()) {
            if (MANDATORY_PROPERTIES.get(key) && (config.get(key) == null || config.get(key).isBlank()))
                throw new IllegalArgumentException("Missing mandatory property: " + key);
        }
    }

    public String get(String propertyName) {
        return read().get(propertyName);
    }
}

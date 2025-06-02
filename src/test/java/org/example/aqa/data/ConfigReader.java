package org.example.aqa.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Getter
public class ConfigReader {
    private static volatile ConfigReader instance;
    private final MyProperties config;
    private ConfigReader() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(System.getProperty("config_path")))) {
            props.load(input);
        }
        String json = mapper.writeValueAsString(props);
        config = mapper.readValue(json, MyProperties.class);
    }

    public static ConfigReader getInstance() throws IOException {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

}
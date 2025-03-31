package org.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String DATA_FILE = "data.json";

    public static void main(String[] args) throws IOException {
        // Загрузка данных из data.json
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> dataList = mapper.readValue(
                new File(DATA_FILE),
                new TypeReference<>(){}
        );

        // Конфигурация WireMock-сервера.
        int port = 9999;
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            try {
                port = Integer.parseInt(portEnv);
            } catch (NumberFormatException ignored) {}
        }

        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(port)
                .extensions(new DynamicResponseTransformer(dataList))
        );
        wireMockServer.start();
        System.out.println("WireMock-сервер запущен на порту " + port);

        // Регистрация стабов для путей /payment и /credit
        wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("^/(payment|credit)$"))
                .willReturn(WireMock.aResponse()
                        .withTransformers("dynamic-response-transformer")
                )
        );
    }

}
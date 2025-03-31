package org.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

public class DynamicResponseTransformer extends ResponseDefinitionTransformer {

    private final List<Map<String, String>> dataList;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DynamicResponseTransformer(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ResponseDefinition transform(Request request,
                                        ResponseDefinition responseDefinition,
                                        FileSource files,
                                        Parameters parameters) {
        try {
            JsonNode jsonRequest = objectMapper.readTree(request.getBodyAsString());
            String number = jsonRequest.path("number").asText(null);

            if (number == null) {
                String errMsg = "Missing field: number";
                return buildResponse(400, errMsg);
            }

            Map<String, String> found = dataList.stream()
                    .filter(item -> number.equals(item.get("number")))
                    .findFirst()
                    .orElse(null);
            if (found == null) {
                String errMsg = "Card number not found";
                return buildResponse(400, errMsg);
            }

            String jsonResponse = objectMapper.writeValueAsString(Map.of(
                    "id", UUID.randomUUID().toString(),
                    "status", found.get("status")
            ));
            return buildResponse(200, jsonResponse);

        } catch (IOException e) {
            String errMsg = "Internal server error: " + e.getMessage();
            return buildResponse(500, errMsg);
        }
    }

    // Формирования ответа с явным указанием заголовков Content-Type и Content-Length.
    private ResponseDefinition buildResponse(int status, String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        return ResponseDefinitionBuilder.responseDefinition()
                .withStatus(status)
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withHeader("Content-Length", String.valueOf(bodyBytes.length))
                .withBody(body)
                .build();
    }

    @Override
    public String getName() {
        return "dynamic-response-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }


}
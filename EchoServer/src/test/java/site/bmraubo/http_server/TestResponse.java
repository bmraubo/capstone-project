package site.bmraubo.http_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import site.bmraubo.http_server.Response;
import site.bmraubo.http_server.ResponseBuilder;

import java.nio.charset.StandardCharsets;

public class TestResponse {

    @Test
    void generateResponseTest() {
        int statusCode = 200;
        String testBody = "Hello World";

        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);

        responseBuilder.setStatusCode(statusCode);
        responseBuilder.setResponseBody(testBody);

        response.generateResponse();

        String expectedResponseLine = "HTTP/1.1 200 OK\r\n";
        String expectedHeaders = "Content-Length: 11\r\n\r\n";
        byte[] expectedBody = testBody.getBytes(StandardCharsets.UTF_8);

        Assertions.assertEquals(expectedResponseLine, response.responseLine);
        Assertions.assertEquals(expectedHeaders, response.responseHeaders);
        Assertions.assertArrayEquals(expectedBody, response.responseBody);
    }
}

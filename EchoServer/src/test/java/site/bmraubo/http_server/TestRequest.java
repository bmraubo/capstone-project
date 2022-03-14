package site.bmraubo.http_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

public class TestRequest {
    String testRequest = "GET /simple_get_with_body HTTP/1.1\r\nContent-Length: 11\r\n\r\nHello World";
    InputStream testInputStream = new ByteArrayInputStream(testRequest.getBytes());
    BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));

    @Test
    void ReadBufferedStreamTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);

        Assertions.assertEquals(testRequest, requestBuilder.requestString);
    }

    @Test
    void RequestLineExtractionTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();
        Assertions.assertEquals("GET /simple_get_with_body HTTP/1.1", requestBuilder.statusLine);

    }

    @Test
    void RequestHeadersExtractionTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();
        Assertions.assertEquals("11", requestBuilder.headers.get("Content-Length"));
    }

    @Test
    void RequestBodyExtractionTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();
        Assertions.assertEquals("Hello World", requestBuilder.body);
    }

    @Test
    void getMethodTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();

        Assertions.assertEquals("GET", requestBuilder.getMethod());
    }

    @Test
    void getURITest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();

        Assertions.assertEquals("/simple_get_with_body", requestBuilder.getURI());
    }

    @Test
    void getProtocolTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();

        Assertions.assertEquals("HTTP/1.1", requestBuilder.getProtocol());
    }

    @Test
    void getHeadersTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();

        LinkedHashMap<String, String> headers = requestBuilder.getHeaders();

        Assertions.assertEquals("11", headers.get("Content-Length"));
    }

    @Test
    void getBodyTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();

        Assertions.assertEquals("Hello World", requestBuilder.getBody());
    }

    @Test
    void parseRequestTest() {


        RequestBuilder requestBuilder = new RequestBuilder();
        Request request = new Request(requestBuilder);

        request.parseRequest(input);

        Assertions.assertEquals("GET", request.method);
        Assertions.assertEquals("/simple_get_with_body", request.uri);
        Assertions.assertEquals("HTTP/1.1", request.protocol);
        Assertions.assertEquals("11", request.headers.get("Content-Length"));
        Assertions.assertEquals("Hello World", request.body);
    }
}

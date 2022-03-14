package site.bmraubo.http_server;

import java.io.BufferedReader;
import java.util.LinkedHashMap;

public class Request {
    RequestBuilder requestBuilder;
    public String method;
    public String uri;
    public String protocol;
    public LinkedHashMap<String, String> headers;
    public String body;

    public Request(RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public void parseRequest(BufferedReader input) {
        requestBuilder.readBufferedStream(input);
        requestBuilder.extractRequest();
        method = requestBuilder.getMethod();
        uri = requestBuilder.getURI();
        protocol = requestBuilder.getProtocol();
        headers = requestBuilder.getHeaders();
        body = requestBuilder.getBody();
    }

}

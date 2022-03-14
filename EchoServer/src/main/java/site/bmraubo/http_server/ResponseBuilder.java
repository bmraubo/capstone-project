package site.bmraubo.http_server;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

public class ResponseBuilder {
    String protocol = "HTTP/1.1";
    String crlf = "\r\n";
    HashMap<Integer, String> statusCodeMap;
    int statusCode;
    String reasonPhrase;
    LinkedHashMap<String, String> headers;
    byte[] responseBody;

    public ResponseBuilder() {
        generateStatusCodeMap();
        generateHeaderMap();
    }


    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        this.reasonPhrase = statusCodeMap.get(statusCode);
    }

    public void setResponseBody(String body) {
        this.responseBody = body.getBytes(StandardCharsets.UTF_8);
        setHeader("Content-Length", calculateContentLength());
    }

    public void setResponseBody(byte[] body) {
        this.responseBody = body;
        setHeader("Content-Length", calculateContentLength());
    }

    public void setHeader(String headerType, String headerValue) {
        headers.put(headerType, headerValue);
    }

    public void setHeader(String headerType, String[] headerValue) {
        String joinedHeaderValue = generateHeaderString(headerValue);
        headers.put(headerType, joinedHeaderValue);
    }

    public String getResponseLine() {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(this.protocol);
        joiner.add(String.valueOf(this.statusCode));
        joiner.add(this.reasonPhrase);
        return joiner + crlf;
    }

    public String getHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            String headerType = key + ": ";
            String header = headerType+value;
            stringBuilder.append(header);
            stringBuilder.append(crlf);
        }
        return stringBuilder + crlf;
    }

    public byte[] getBody() {
        return responseBody;
    }

    private void generateStatusCodeMap() {
        statusCodeMap = new HashMap<Integer, String>();
        statusCodeMap.put(200, "OK");
        statusCodeMap.put(201, "Created");
        statusCodeMap.put(204, "No Content");
        statusCodeMap.put(301, "Moved Permanently");
        statusCodeMap.put(400, "Bad Request");
        statusCodeMap.put(404, "Not Found");
        statusCodeMap.put(405, "Method Not Allowed");
        statusCodeMap.put(408, "Request Time-out");
        statusCodeMap.put(415, "Unsupported Media Type");
        statusCodeMap.put(500, "Internal Server Error");
    }

    private void generateHeaderMap() {
        headers = new LinkedHashMap<String, String>();
    }

    private String generateHeaderString(String[] headerValue) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String x : headerValue) {
            joiner.add(x);
        }
        return joiner.toString();
    }

    private String calculateContentLength() {
        return String.valueOf(responseBody.length);
    }
}

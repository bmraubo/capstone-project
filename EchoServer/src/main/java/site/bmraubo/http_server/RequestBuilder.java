package site.bmraubo.http_server;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class RequestBuilder {
    String requestString;
    String[] requestArray;
    String statusLine;
    LinkedHashMap<String, String> headers;
    String body;
    boolean requestIncludesHeaders;
    boolean requestIncludesBody;

    public RequestBuilder(){
        generateHeaderMap();
    }

    public void readBufferedStream(BufferedReader input) {
        try {
            System.out.println("looking at data");
            String data = "";
            while ((input.ready())) {
                data = data + Character.toString(input.read());
            }
            System.out.println("Data read");
            System.out.println(data);
            requestString = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extractRequest(){
        requestArray = requestString.split("\r\n");
        System.out.println("REQUEST String" + requestString);
        System.out.println("REQUEST ARRAY" + requestArray);
        extractStatusLine();
        requestIncludesHeaders = checkForHeaders();
        requestIncludesBody = checkForBody();
        if (requestIncludesHeaders) {
            extractHeaders();
        }
        if (requestIncludesBody) {
            extractBody();
        }
    }

    public String getMethod() {
        return statusLine.split(" ")[0];
    }

    public String getURI() {
        return statusLine.split(" ")[1];
    }

    public String getProtocol() {
        return statusLine.split(" ")[2];
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private void extractStatusLine() {
        statusLine = requestArray[0];
    }

    private void extractHeaders() {
        if (requestIncludesBody) {
            for (String x : Arrays.copyOfRange(requestArray, 1, requestArray.length - 2)) {
                String headerKey = extractHeaderKey(x);
                String headerValue = extractHeaderValue(x);
                headers.put(headerKey, headerValue);
            }
        } else {
            for (String x : Arrays.copyOfRange(requestArray, 1, requestArray.length - 1 )) {
                String headerKey = extractHeaderKey(x);
                String headerValue = extractHeaderValue(x);
                headers.put(headerKey, headerValue);
            }
        }
    }

    private String extractHeaderKey(String header) {
        return header.substring(0, header.indexOf(":"));
    }

    private String extractHeaderValue(String header) {
        return header.substring(header.indexOf(" ") + 1, header.length());
    }

    private void extractBody() {
        System.out.println(requestArray[requestArray.length-1]);
        body = requestArray[requestArray.length-1];

    }

    private void generateHeaderMap() {
        headers = new LinkedHashMap<String, String>();
    }

    private boolean checkForBody() {
        for (String x : requestArray) {
            System.out.println(x);
            if (x.startsWith("Content-Length")) {
                System.out.println("Body Identified");
                return true;
            }
        }
        System.out.println("No Body Identified");
        return false;
    }

    private boolean checkForHeaders() {
        return requestArray.length > 1;
    }
}

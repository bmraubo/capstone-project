package site.bmraubo.http_server;

public class Response {
    ResponseBuilder responseBuilder;

    public String responseLine;
    public String responseHeaders;
    public byte[] responseBody;

    public Response(ResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    public void generateResponse() {
        this.responseLine = responseBuilder.getResponseLine();
        this.responseHeaders = responseBuilder.getHeaders();
        this.responseBody = responseBuilder.getBody();
    }
}

package site.bmraubo.http_server;

public interface Endpoint {

    Response prepareResponse(Request request);
}

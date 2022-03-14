package site.bmraubo.echo_server_endpoints;

import site.bmraubo.http_server.Endpoint;
import site.bmraubo.http_server.Request;
import site.bmraubo.http_server.Response;
import site.bmraubo.http_server.ResponseBuilder;

public class Redirect implements Endpoint {
    String[] allowedMethods = {"GET"};
    String contentLocation = "http://127.0.0.1:5000/simple_get";

    @Override
    public Response prepareResponse(Request request) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        System.out.println("Redirecting");
        responseBuilder.setStatusCode(301);
        responseBuilder.setHeader("Location", contentLocation);
        responseBuilder.setResponseBody("");
        return response;
    }
}

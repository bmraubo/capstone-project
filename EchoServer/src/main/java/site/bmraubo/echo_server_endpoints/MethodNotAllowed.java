package site.bmraubo.echo_server_endpoints;

import site.bmraubo.http_server.Response;
import site.bmraubo.http_server.ResponseBuilder;

public class MethodNotAllowed {

    public static Response prepareResponse(String[] allowedMethods) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        System.out.println("Method not allowed!");
        responseBuilder.setStatusCode(405);
        responseBuilder.setHeader("Allow", allowedMethods);
        responseBuilder.setResponseBody("");
        return response;
    }
}

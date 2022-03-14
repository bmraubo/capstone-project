package site.bmraubo.echo_server_endpoints;

import site.bmraubo.http_server.Endpoint;
import site.bmraubo.http_server.Request;
import site.bmraubo.http_server.Response;
import site.bmraubo.http_server.ResponseBuilder;

public class EchoBody implements Endpoint {
    String[] allowedMethods = {"POST", "OPTIONS"};

    public Response prepareResponse(Request request) {
        if (request.method.equals("POST")) {
            return simplePostRequest(request);
        } else {
            return MethodNotAllowed.prepareResponse(allowedMethods);
        }
    }

    private Response simplePostRequest(Request request) {
        System.out.println("Simple Post identified");
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(200);
        responseBuilder.setResponseBody(request.body);
        return response;
    }
}

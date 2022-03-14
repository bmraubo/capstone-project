package site.bmraubo.echo_server_endpoints;

import org.json.JSONObject;
import site.bmraubo.http_server.Endpoint;
import site.bmraubo.http_server.Request;
import site.bmraubo.http_server.Response;
import site.bmraubo.http_server.ResponseBuilder;

public class JSONResponse implements Endpoint {
    JSONObject responseBody = generateJSONResponse();
    String contentType = "application/json;charset=utf-8";

    @Override
    public Response prepareResponse(Request request) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(200);
        responseBuilder.setHeader("Content-Type", contentType);
        responseBuilder.setResponseBody(responseBody.toString());
        return response;
    }

    private JSONObject generateJSONResponse() {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("key1", "value1");
        jsonResponse.put("key2", "value2");
        return jsonResponse;
    }
}

package site.bmraubo.http_server;

public class BadRequest implements ErrorHandler{

    @Override
    public Response prepareResponse() {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(400);
        return response;
    }
}

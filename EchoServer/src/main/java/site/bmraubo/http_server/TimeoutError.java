package site.bmraubo.http_server;

public class TimeoutError implements ErrorHandler{


    @Override
    public Response prepareResponse() {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(408);
        return response;
    }
}

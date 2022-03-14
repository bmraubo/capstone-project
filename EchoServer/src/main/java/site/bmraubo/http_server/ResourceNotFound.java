package site.bmraubo.http_server;

public class ResourceNotFound implements ErrorHandler{
    String errorMessage = "Resource not Found";

    public Response prepareResponse() {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        System.out.println(errorMessage);
        responseBuilder.setStatusCode(404);
        responseBuilder.setResponseBody("");
        return response;
    }
}

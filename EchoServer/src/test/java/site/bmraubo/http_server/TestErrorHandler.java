package site.bmraubo.http_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import site.bmraubo.http_server.*;

public class TestErrorHandler {

    @Test
    void ServerErrorTest() {
        String errorMessage = "Request read as empty, please try again.";

        ServerError serverError = new ServerError(errorMessage);
        Response response = serverError.prepareResponse();

        Assertions.assertEquals(500, response.responseBuilder.statusCode);
        Assertions.assertEquals("Internal Server Error", response.responseBuilder.reasonPhrase);
    }

    @Test
    void TimeoutErrorTest() {
        TimeoutError timeoutError = new TimeoutError();
        Response response = timeoutError.prepareResponse();

        Assertions.assertEquals(408, response.responseBuilder.statusCode);
        Assertions.assertEquals("Request Time-out", response.responseBuilder.reasonPhrase);
    }

    @Test
    void BadRequestErrorTest() {
        BadRequest badRequest = new BadRequest();
        Response response = badRequest.prepareResponse();

        Assertions.assertEquals(400, response.responseBuilder.statusCode);
        Assertions.assertEquals("Bad Request", response.responseBuilder.reasonPhrase);
    }

    @Test
    void ResourceNotFoundTest() {
        ResourceNotFound resourceNotFound = new ResourceNotFound();
        Response response = resourceNotFound.prepareResponse();

        Assertions.assertEquals(404, response.responseBuilder.statusCode);
        Assertions.assertEquals("Not Found", response.responseBuilder.reasonPhrase);
    }
}

package site.bmraubo.http_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import site.bmraubo.echo_server_endpoints.TextResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestRouter {

    @Test
    void addRouteTest() {
        Router testRouter = new Router();
        TextResponse textResponse = new TextResponse();
        testRouter.addRoute("/text_response", textResponse);

        Assertions.assertEquals(textResponse, testRouter.routes.get("/text_response"));
    }

    @Test
    void connectTest() {
        String testRequestData = "GET /route_spy HTTP/1.1\r\n\r\n";
        InputStream testInputStream = new ByteArrayInputStream(testRequestData.getBytes());
        BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));

        RequestBuilder requestBuilder = new RequestBuilder();
        Request testRequest = new Request(requestBuilder);
        testRequest.parseRequest(input);

        Router testRouter = new Router();
        RouteSpy routeSpy = new RouteSpy();
        testRouter.addRoute("/route_spy", routeSpy);

        testRouter.connect(testRequest);

        Assertions.assertTrue(routeSpy.endpointReached);
    }
}

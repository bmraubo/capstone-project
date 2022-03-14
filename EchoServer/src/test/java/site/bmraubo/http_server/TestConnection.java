package site.bmraubo.http_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import site.bmraubo.echo_server_endpoints.SimpleGet;

import java.io.*;

public class TestConnection {

    @Test
    void TestOpenIOStreams() {
        String testRequest = "GET /simple_get HTTP/1.1\r\n";
        InputStream testInputStream = new ByteArrayInputStream(testRequest.getBytes());
        BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));
        PrintWriter output = new PrintWriter(new StringWriter());

        Router router = new Router();
        router.addRoute("/simple_get", new SimpleGet());

        ConnectionSpy connectionSpy = new ConnectionSpy(input, output, router);
        connectionSpy.processRequest();

        Assertions.assertTrue(connectionSpy.openedIOStreams);
    }

    @Test
    void BuildRequestTest() {
        String testRequest = "GET /simple_get HTTP/1.1\r\n";
        InputStream testInputStream = new ByteArrayInputStream(testRequest.getBytes());
        BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));
        PrintWriter output = new PrintWriter(new StringWriter());

        Router router = new Router();
        router.addRoute("/simple_get", new SimpleGet());

        ConnectionSpy connectionSpy = new ConnectionSpy(input, output, router);
        connectionSpy.processRequest();

        Assertions.assertTrue(connectionSpy.requestBuilt);
    }

    @Test
    void RouteConnectionTest() {
        String testRequest = "GET /simple_get HTTP/1.1\r\n";
        InputStream testInputStream = new ByteArrayInputStream(testRequest.getBytes());
        BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));
        PrintWriter output = new PrintWriter(new StringWriter());

        Router router = new Router();
        router.addRoute("/simple_get", new SimpleGet());

        ConnectionSpy connectionSpy = new ConnectionSpy(input, output, router);
        connectionSpy.processRequest();

        Assertions.assertTrue(connectionSpy.connectionRouted);
    }

    @Test
    void SendResponseTest() {
        String testRequest = "GET /simple_get HTTP/1.1\r\n";
        InputStream testInputStream = new ByteArrayInputStream(testRequest.getBytes());
        BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));
        PrintWriter output = new PrintWriter(new StringWriter());

        Router router = new Router();
        router.addRoute("/simple_get", new SimpleGet());

        ConnectionSpy connectionSpy = new ConnectionSpy(input, output, router);
        connectionSpy.processRequest();

        Assertions.assertTrue(connectionSpy.responseSent);
        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", connectionSpy.responseLine);
    }

    @Test
    void CloseConnectionTest() {
        String testRequest = "GET /simple_get HTTP/1.1\r\n";
        InputStream testInputStream = new ByteArrayInputStream(testRequest.getBytes());
        BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));
        PrintWriter output = new PrintWriter(new StringWriter());

        Router router = new Router();
        router.addRoute("/simple_get", new SimpleGet());

        ConnectionSpy connectionSpy = new ConnectionSpy(input, output, router);
        connectionSpy.processRequest();

        Assertions.assertTrue(connectionSpy.connectionClosed);
    }
}

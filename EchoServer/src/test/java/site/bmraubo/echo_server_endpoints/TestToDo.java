package site.bmraubo.echo_server_endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import site.bmraubo.http_server.Endpoint;
import site.bmraubo.http_server.Request;
import site.bmraubo.http_server.RequestBuilder;
import site.bmraubo.http_server.Response;
import site.bmraubo.todo.LocalMemoryTaskList;
import site.bmraubo.todo.TaskList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestToDo {

    private Request setUpRequest(String requestString) {
        InputStream testInputStream = new ByteArrayInputStream(requestString.getBytes());
        BufferedReader input = new BufferedReader(new InputStreamReader(testInputStream));

        RequestBuilder requestBuilder = new RequestBuilder();
        Request request = new Request(requestBuilder);
        request.parseRequest(input);

        return request;
    }

    @Test
    void contentTypeValidationTest() {
        String testRequest = "POST /todo HTTP/1.1\r\n" +
                "Content-Type: application/json\r\n" +
                "Connection: close\r\n" +
                "Host: 127.0.0.1:5000\r\n" +
                "User-Agent: http.rb/4.3.0\r\n" +
                "Content-Length: 21\r\n" +
                "\r\n" +
                "{\"task\":\"a new task\"}";

        Request request = setUpRequest(testRequest);
        TaskList taskList = new LocalMemoryTaskList();

        Endpoint endpoint = new ToDo(taskList);
        Response response = endpoint.prepareResponse(request);
        response.generateResponse();

        Assertions.assertEquals("HTTP/1.1 201 Created\r\n", response.responseLine);
        Assertions.assertEquals(request.body, taskList.viewTaskByID(1).taskInfo);

    }

    @Test
    void contentValueValidationTest() {
        String testRequest = "POST /todo HTTP/1.1\r\n" +
                "Content-Type: application/xml\r\n" +
                "Connection: close\r\n" +
                "Host: 127.0.0.1:5000\r\n" +
                "User-Agent: http.rb/4.3.0\r\n" +
                "Content-Length: 10\r\n" +
                "\r\n" +
                "a new task";

        Request request = setUpRequest(testRequest);
        TaskList taskList = new LocalMemoryTaskList();

        Endpoint endpoint = new ToDo(taskList);
        Response response = endpoint.prepareResponse(request);
        response.generateResponse();

        Assertions.assertEquals("HTTP/1.1 400 Bad Request\r\n", response.responseLine);
    }

    @Test
    void validateUnsupportedMediaTypeTest() {
        String testRequest = "POST /todo HTTP/1.1\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "Host: 127.0.0.1:5000\r\n" +
                "User-Agent: http.rb/4.3.0\r\n" +
                "Content-Length: 10\r\n" +
                "\r\n" +
                "a new task";

        Request request = setUpRequest(testRequest);
        TaskList taskList = new LocalMemoryTaskList();

        Endpoint endpoint = new ToDo(taskList);
        Response response = endpoint.prepareResponse(request);
        response.generateResponse();

        Assertions.assertEquals("HTTP/1.1 415 Unsupported Media Type\r\n", response.responseLine);
    }
}

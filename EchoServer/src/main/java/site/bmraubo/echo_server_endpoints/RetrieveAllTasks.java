package site.bmraubo.echo_server_endpoints;

import org.json.JSONArray;
import site.bmraubo.http_server.Endpoint;
import site.bmraubo.http_server.Request;
import site.bmraubo.http_server.Response;
import site.bmraubo.http_server.ResponseBuilder;
import site.bmraubo.todo.TaskList;

public class RetrieveAllTasks implements Endpoint {
    TaskList taskList;
    JSONArray taskListJSON;
    String contentType = "application/json;charset=utf-8";

    public RetrieveAllTasks(TaskList taskList) {
        this.taskList = taskList;
    }

    @Override
    public Response prepareResponse(Request request) {
        this.taskListJSON = taskList.getAllTasks();
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(200);
        responseBuilder.setHeader("Content-Type", contentType);
        responseBuilder.setHeader("Access-Control-Allow-Origin", "*");
        responseBuilder.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS");
        responseBuilder.setResponseBody(taskListJSON.toString());
        return response;
    }
}


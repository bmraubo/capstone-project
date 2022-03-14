package site.bmraubo.echo_server_endpoints;

import site.bmraubo.http_server.*;
import site.bmraubo.todo.Task;
import site.bmraubo.todo.TaskList;
import site.bmraubo.todo.TaskMaster;

import java.nio.charset.StandardCharsets;

public class ToDo implements Endpoint {
    TaskList taskList;

    public ToDo(TaskList taskList) {
        this.taskList = taskList;
    }

    @Override
    public Response prepareResponse(Request request) {
        if (validateContentType(request) && validateValues(request)) {
            Task task = addTask(request.body);
            return successfulResponse(task);
        } else if (validateContentType(request) && !validateValues(request)) {
            return unsuccessfulResponse(400);
        } else {
            return unsuccessfulResponse(415);
        }
    }

    private Response successfulResponse(Task task) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(201);
        responseBuilder.setHeader("Content-Type", "application/json;charset=utf-8");
        responseBuilder.setHeader("Access-Control-Allow-Origin", "*");
        responseBuilder.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS");
        responseBuilder.setResponseBody(task.taskJSON.toString().getBytes(StandardCharsets.UTF_8));
        return response;
    }

    private Response unsuccessfulResponse(int statusCode) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(statusCode);
        return response;
    }

    private boolean validateContentType(Request request) {
        return request.headers.get("Content-Type").contains("application");
    }

    private boolean validateValues(Request request) {
        return request.body.contains(":") && request.body.contains("{") && request.body.contains("}");
    }

    private Task addTask(String taskInfo) {
        TaskMaster taskMaster = new TaskMaster();
        taskMaster.openTaskList(taskList);
        Task task = taskMaster.addTask(taskInfo);
        return task;
    }


}

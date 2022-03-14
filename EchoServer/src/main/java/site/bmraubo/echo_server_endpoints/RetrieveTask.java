package site.bmraubo.echo_server_endpoints;

import org.json.JSONObject;
import site.bmraubo.http_server.*;
import site.bmraubo.todo.Task;
import site.bmraubo.todo.TaskList;
import site.bmraubo.todo.TaskMaster;

public class RetrieveTask implements Endpoint {
    TaskList taskList;

    public RetrieveTask(TaskList taskList) {
        this.taskList = taskList;
    }

    @Override
    public Response prepareResponse(Request request) {
        int taskID = getTaskID(request.uri);
        return switch (request.method) {
            case ("GET") -> processGetRequest(request, taskID);
            case ("PUT") -> processPutRequest(request, taskID);
            case ("DELETE") -> processDeleteRequest(taskID);
            case ("OPTIONS") -> processOptionsRequest();
            default -> new ResourceNotFound().prepareResponse();
        };
    }

    private Response processGetRequest(Request request, int taskID) {
        if (taskExists(taskID)) {
            Task task = getTaskInfo(taskID);
            return successfulGetRequest(request, task);
        } else {
            return new ResourceNotFound().prepareResponse();
        }
    }

    private Response processPutRequest(Request request, int taskID) {
        if (validateContentType(request) && validateValues(request)) {
            if (updateTask(taskID, request.body)) {
                return successfulPutResponse(request, taskID);
            } else {
                return new ServerError("Database Error").prepareResponse();
            }
        } else if (validateContentType(request) && !validateValues(request)) {
            System.out.println("Failed to validate JSON values");
            System.out.println("CONTENT BODY: "+request.body);
            return unsuccessfulResponse(400);
        } else {
            System.out.println("Failed to validate Content Type");
            return unsuccessfulResponse(415);
        }
    }

    private Response processDeleteRequest(int taskID) {
        if (taskExists(taskID)) {
            if (removeTask(taskID)) {
                return successfulDeleteResponse();
            } else {
                return new ServerError("Database Error").prepareResponse();
            }
        } else {
            return unsuccessfulResponse(204);
        }
    }

    private Response successfulGetRequest(Request request, Task task) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(200);
        responseBuilder.setHeader("Content-Type", "application/json;charset=utf-8");
        setCORSHeaders(responseBuilder);
        responseBuilder.setResponseBody(task.taskInfo);
        return response;
    }

    private Response successfulPutResponse(Request request, int taskID) {
        Task task = taskList.viewTaskByID(taskID);
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(200);
        responseBuilder.setHeader("Content-Type", "application/json;charset=utf-8");
        setCORSHeaders(responseBuilder);
        responseBuilder.setResponseBody(task.taskJSON.toString());
        return response;
    }

    private Response successfulDeleteResponse() {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(204);
        responseBuilder.setResponseBody("");
        setCORSHeaders(responseBuilder);
        return response;
    }

    private Response unsuccessfulResponse(int statusCode) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        responseBuilder.setStatusCode(statusCode);
        return response;
    }

    private Response processOptionsRequest() {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = new Response(responseBuilder);
        setCORSHeaders(responseBuilder);
        responseBuilder.setStatusCode(200);
        responseBuilder.setResponseBody("");
        return response;
    }

    private int getTaskID(String uri) {
        return Integer.parseInt(uri.substring(6));
    }

    private Task getTaskInfo(int taskID) {
        TaskMaster taskMaster= new TaskMaster();
        taskMaster.openTaskList(taskList);
        return taskMaster.viewTask(taskID);
    }

    private boolean updateTask(int taskID, String taskInfo) {
        TaskMaster taskMaster = new TaskMaster();
        JSONObject taskData = new JSONObject(taskInfo);
        System.out.println(taskData);
        taskMaster.openTaskList(taskList);
        taskMaster.updateTask(taskID, taskData);
        return taskMaster.checkActionOutcome();
    }

    private boolean removeTask(int taskID) {
        TaskMaster taskMaster = new TaskMaster();
        taskMaster.openTaskList(taskList);
        taskMaster.removeTask(taskID);
        return taskMaster.checkActionOutcome();
    }

    private boolean validateContentType(Request request) {
        return request.headers.get("Content-Type").contains("application");
    }

    private boolean validateValues(Request request) {
        System.out.println(request.body);
        return request.body.contains(":") && request.body.contains("{") && request.body.contains("}");
    }

    private boolean taskExists(int id) {
        return taskList.viewTaskByID(id) != null;
    }

    private void setCORSHeaders(ResponseBuilder responseBuilder) {
        responseBuilder.setHeader("Access-Control-Allow-Origin", "*");
        responseBuilder.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS");
        responseBuilder.setHeader("Access-Control-Allow-Headers", "*");
    }
}

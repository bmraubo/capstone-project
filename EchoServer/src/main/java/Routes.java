import site.bmraubo.echo_server_endpoints.*;
import site.bmraubo.http_server.Router;
import site.bmraubo.todo.PostgresTaskList;
import site.bmraubo.todo.TaskList;

public class Routes {

    static Router assignRoutes() {
        TaskList taskList = new PostgresTaskList();

        Router router = new Router();
        router.addRoute("/simple_get", new SimpleGet());
        router.addRoute("/simple_get_with_body", new SimpleGetWithBody());
        router.addRoute("/echo_body", new EchoBody());
        router.addRoute("/head_request", new HeadRequest());
        router.addRoute("/method_options", new MethodOptions());
        router.addRoute("/method_options2", new MethodOptions2());
        router.addRoute("/redirect", new Redirect());
        router.addRoute("/text_response", new TextResponse());
        router.addRoute("/html_response", new HTMLResponse());
        router.addRoute("/json_response", new JSONResponse());
        router.addRoute("/xml_response", new XMLResponse());
        router.addRoute("/health-check.html", new HealthCheck());
        router.addRoute("/kitteh.jpg", new Kitteh());
        router.addRoute("/doggo.png", new Doggo());
        router.addRoute("/kisses.gif", new Kisses());
        router.addRoute("/todo", new ToDo(taskList));
        router.addRoute("/todo/.+", new RetrieveTask(taskList));
        router.addRoute("/todos", new RetrieveAllTasks(taskList));
        return router;
    }
}

package site.bmraubo.http_server;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Router {
    public HashMap<String, Endpoint> routes;

    public Router() {
        routes = generateRouteMap();
    }

    public void addRoute(String uri, Endpoint endpoint) {
        routes.put(uri, endpoint);
    }

    public Response connect(Request request) {
        Endpoint endpoint = findRoute(request);
        if (endpoint == null) {
            ResourceNotFound resourceNotFound = new ResourceNotFound();
            return resourceNotFound.prepareResponse();
        }
        return endpoint.prepareResponse(request);
    }

    private Endpoint findRoute(Request request) {
        for (String key : routes.keySet()) {
            if (request.uri.matches(key)) {
                return routes.get(key);
            }
        }
        return null;
    }

    private LinkedHashMap<String, Endpoint> generateRouteMap() {
        return new LinkedHashMap<String, Endpoint>();
    }

    static Response serverError(String errorMessage) {
        ServerError error = new ServerError(errorMessage);
        return error.prepareResponse();
    }
}

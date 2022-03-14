package site.bmraubo.http_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements ConnectionWrapper, Runnable{
    Socket socket;
    Router router;
    BufferedReader input;
    PrintWriter output;

    Request request;
    Response response;

    public Connection(Socket socket, Router router) {
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void processRequest() {
        waitForData();
        openIOStreams();
        buildRequest();
        routeConnection();
        sendResponse();
        closeConnection();
    }

    @Override
    public void openIOStreams() {
        try {
            InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
            input = new BufferedReader(inputStream);
            output = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("I/O Streams opened");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildRequest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        request = new Request(requestBuilder);
        request.parseRequest(input);
    }

    @Override
    public void routeConnection() {
        response = router.connect(request);
    }

    @Override
    public void sendResponse() {
        try {
            response.generateResponse();
            System.out.println("Sending Response...");
            output.print(response.responseLine);
            output.print(response.responseHeaders);
            output.flush();
            if (response.responseBody != null) {
                OutputStream output = socket.getOutputStream();
                output.write(response.responseBody);
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            System.out.println("Closing IO Streams and Socket");
            input.close();
            output.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForData() {
        try {
            int timeoutCounter = 0;
            while ((socket.getInputStream().available() == 0) && (timeoutCounter < 1000)) {
                Thread.sleep(5);
                timeoutCounter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        processRequest();
        System.out.println("Thread complete");
    }
}

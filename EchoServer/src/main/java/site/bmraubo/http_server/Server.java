package site.bmraubo.http_server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    Router router;
    boolean running = true;


    public Server(Router router) {
        this.router = router;
    }

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port, 5, InetAddress.getByName("0.0.0.0"));
        while (running) {
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(new Connection(socket, router));
            thread.start();
            System.out.println("Thread Started");
        }
    }

}

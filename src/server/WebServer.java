package server;

import server.auth.AuthenticationManager;
import server.request.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer implements AutoCloseable {
    private final ServerSocket serverSocket;
    private final String documentRoot;
    private final AuthenticationManager authManager;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java server.WebServer <port> <document root>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        String docRoot = args[1];
        try (WebServer server = new WebServer(port, docRoot, new AuthenticationManager(docRoot))) {
            server.listen();
        }
    }

    public WebServer(int port, String documentRoot, AuthenticationManager authManager) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.documentRoot = documentRoot;
        this.authManager = authManager;
        System.out.println("Server started on port " + port + " with document root: " + documentRoot);
    }

    public void listen() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                Thread thread = new Thread(new RequestHandler(clientSocket, documentRoot, authManager));
                thread.start();
            } catch (IOException e) {
                System.err.println("Error accepting connection: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
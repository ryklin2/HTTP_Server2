package server.request;

import server.auth.AuthenticationManager;
import server.auth.AuthenticationResponse;
import server.config.MimeTypes;
import server.err.HTTPException;
import server.http.HTTPRequest;
import server.http.HTTPMethod;
import server.response.*;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final String documentRoot;
    private final AuthenticationManager authManager;

    public RequestHandler(Socket socket, String documentRoot, AuthenticationManager authManager) {
        this.socket = socket;
        this.documentRoot = documentRoot;
        this.authManager = authManager;
    }

    @Override
    public void run() {
        Response response = null;
        try {
            HTTPRequest request = new HTTPRequest();
            request.parse(socket.getInputStream());
            String path = request.getRequestLine().getRequestTarget();
            Path resolvedPath = resolvePath(path);

            String authHeader = request.getHeaders().getAuthorizationHeader();
            AuthenticationResponse authResponse = authManager.checkAuth(path, authHeader);
            if (authResponse == AuthenticationResponse.UNAUTHORIZED) {
                response = new Unauthorized(documentRoot);
            } else if (authResponse == AuthenticationResponse.FORBIDDEN) {
                response = new Forbidden(documentRoot);
            } else {
                HTTPMethod method = request.getRequestLine().getMethod();
                switch (method) {
                    case GET:
                        response = handleGet(resolvedPath);
                        break;
                    case HEAD:
                        response = handleHead(resolvedPath);
                        break;
                    case PUT:
                        response = handlePut(request, resolvedPath);
                        break;
                    case DELETE:
                        response = handleDelete(resolvedPath);
                        break;
                    default:
                        response = new BadRequest(documentRoot);
                        break;
                }
            }
            response.send(socket.getOutputStream());
        } catch (HTTPException e) {
            response = new BadRequest(documentRoot);
            try {
                response.send(socket.getOutputStream());
            } catch (IOException ex) {
                System.err.println("Error sending 400 response: " + ex.getMessage());
            }
        } catch (IOException e) {
            response = new InternalServerError(documentRoot);
            try {
                response.send(socket.getOutputStream());
            } catch (IOException ex) {
                System.err.println("Error sending 500 response: " + ex.getMessage());
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private Path resolvePath(String requestPath) throws HTTPException {
        Path base = Paths.get(documentRoot).toAbsolutePath().normalize();
        String cleanPath = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;
        Path requested = base.resolve(cleanPath).normalize();
        if (!requested.startsWith(base)) {
            throw new HTTPException(403, "Forbidden");
        }
        return requested;
    }

    private Response handleGet(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            path = path.resolve("index.html");
        }
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return new Ok(documentRoot, path);
        } else {
            return new NotFound(documentRoot);
        }
    }

    private Response handleHead(Path path) throws IOException {
        return handleGet(path); // Ok class handles HEAD by sending headers only if needed
    }

    private Response handlePut(HTTPRequest request, Path path) throws IOException {
        String contentLengthStr = request.getHeaders().get("content-length");
        System.out.println("PUT request for path: " + path);
        System.out.println("Content-Length header: " + contentLengthStr);

        if (contentLengthStr == null || contentLengthStr.trim().isEmpty()) {
            System.err.println("PUT request missing Content-Length header");
            return new BadRequest(documentRoot);
        }

        int contentLength;
        try {
            contentLength = Integer.parseInt(contentLengthStr.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid Content-Length: " + contentLengthStr);
            return new BadRequest(documentRoot);
        }

        byte[] body = request.getBody().getContent();
        System.out.println("Processing PUT - actual body length: " + body.length +
                " of expected " + contentLength);

        if (body.length == 0 && contentLength > 0) {
            System.err.println("Warning: PUT request body empty despite Content-Length: " +
                    contentLength);
            // We'll continue anyway, creating an empty file
        }

        // Ensure parent directories exist
        Files.createDirectories(path.getParent());

        // Write the file
        Files.write(path, body);
        System.out.println("PUT created file: " + path.toString() +
                " with content length: " + body.length);

        return new Created(documentRoot);
    }

    private Response handleDelete(Path path) throws IOException {
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return new NotFound(documentRoot);
        }
        Files.delete(path);
        return new NoContent(documentRoot);
    }
}
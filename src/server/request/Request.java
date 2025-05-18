package server.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;


    public Request(InputStream inputStream) throws IOException, InvalidRequestException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();
        // check if empty
        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new InvalidRequestException("Empty request line");
        }
        //split it into chunks, \\s was recommended regex in case there's multiple spaces
        String[] parts = requestLine.split("\\s+", 3);
        if (parts.length != 3) {
            throw new InvalidRequestException("Invalid request line: " + requestLine);
        }
        // set them into method, path, version
        this.method = parts[0].toUpperCase();
        this.path = parts[1];
        this.version = parts[2];

        //split head parts if needed
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // byteReader
        String contentLengthStr = headers.get("Content-Length");
        if (contentLengthStr != null) {
            int contentLength = Integer.parseInt(contentLengthStr);
            body = new byte[contentLength];
            int bytesRead = 0;
            while (bytesRead < contentLength) {
                int read = inputStream.read(body, bytesRead, contentLength - bytesRead);
                if (read == -1) break;
                bytesRead += read;
            }
        }
    }

    // Getters
    public String getMethod() {
        return method;
    }
    public String getPath() {
        return path;
    }
    public String getVersion() {
        return version;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public byte[] getBody() {
        return body;
    }
}
class InvalidRequestException extends Exception {
    public InvalidRequestException(String message) {
        super(message);
    }
}


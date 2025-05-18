package server.http;

import server.err.HTTPException;

public class RequestLine {
    private HTTPMethod method;
    private String requestTarget;
    private String httpVersion;

    public RequestLine() {}

    public void parse(String line) throws HTTPException {
        if (line == null) throw new HTTPException(400, "Request line is null");
        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new HTTPException(400, "Invalid request line: " + line);
        }
        String methodStr = parts[0].trim().toUpperCase();
        if (!HTTPMethod.isValid(methodStr)) {
            throw new HTTPException(400, "Invalid HTTP method: " + methodStr);
        }
        this.method = HTTPMethod.valueOf(methodStr);
        this.requestTarget = parts[1].trim();
        this.httpVersion = parts[2].trim();
    }

    public HTTPMethod getMethod() { return method; }
    public String getRequestTarget() { return requestTarget; }
    public String getHttpVersion() { return httpVersion; }
}
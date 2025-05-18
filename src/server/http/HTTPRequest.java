package server.http;

import java.io.*;

import server.err.HTTPException;

public class HTTPRequest {
    private RequestLine requestLine;
    private Headers headers;
    private Body body;

    public HTTPRequest() {
        this.requestLine = new RequestLine();
        this.headers = new Headers();
        this.body = new Body();
    }

    //added a check to help log parses and errors
    public void parse(InputStream input) throws IOException, HTTPException {
        // First, read the request line directly from the input stream
        StringBuilder requestLine = new StringBuilder();
        int b;
        while ((b = input.read()) != -1) {
            char c = (char) b;
            requestLine.append(c);
            if (c == '\n' && requestLine.toString().endsWith("\r\n")) {
                break;
            }
        }

        String requestLineStr = requestLine.toString().trim();
        System.out.println("Received request line: " + requestLineStr);
        this.requestLine.parse(requestLineStr);

        // Now read headers directly
        StringBuilder headerSection = new StringBuilder();
        boolean inHeaderSection = true;
        int consecutiveNewlines = 0;

        while (inHeaderSection && (b = input.read()) != -1) {
            char c = (char) b;
            headerSection.append(c);

            if (c == '\n') {
                consecutiveNewlines++;
                if (headerSection.toString().endsWith("\r\n\r\n")) {
                    // End of headers section
                    inHeaderSection = false;
                }
            } else if (c != '\r') {
                consecutiveNewlines = 0;
            }
        }

        // Parse the headers from the collected string
        BufferedReader headerReader = new BufferedReader(new StringReader(headerSection.toString()));
        this.headers.parse(headerReader);

        // Now handle the body
        String contentLengthStr = this.headers.get("content-length");
        if (contentLengthStr != null) {
            try {
                int contentLength = Integer.parseInt(contentLengthStr.trim());
                if (contentLength > 0) {
                    System.out.println("Parsing body with Content-Length: " + contentLength);
                    // Now the input stream should be positioned at the start of the body
                    this.body.parse(input, contentLength);
                }
            } catch (NumberFormatException e) {
                throw new HTTPException(400, "Invalid content length: " + contentLengthStr);
            }
        } else {
            System.out.println("No Content-Length header found");
        }
    }


    public RequestLine getRequestLine() { return requestLine; }
    public Headers getHeaders() { return headers; }
    public Body getBody() { return body; }
}
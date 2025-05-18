package server.response;

import java.io.IOException;
import java.io.OutputStream;

public class InternalServerError extends Response {
    public InternalServerError(String documentRoot) {
        super(documentRoot, 500, "Internal Server Error");
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        headers.set("Content-Type", "text/html");
        body.setContent("<h1>500 Internal Server Error</h1>".getBytes());

        outputStream.write((statusLine.toString() + "\r\n").getBytes("ISO-8859-1"));
        outputStream.write(headers.toByteArray());
        outputStream.write(body.getContent());
        outputStream.flush();
    }
}
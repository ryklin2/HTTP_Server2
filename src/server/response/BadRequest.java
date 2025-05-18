package server.response;

import java.io.IOException;
import java.io.OutputStream;

public class BadRequest extends Response {
    public BadRequest(String documentRoot) {
        super(documentRoot, 400, "Bad Request");
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        headers.set("Content-Type", "text/html");
        body.setContent("<h1>400 Bad Request</h1>".getBytes());

        outputStream.write((statusLine.toString() + "\r\n").getBytes("ISO-8859-1"));
        outputStream.write(headers.toByteArray());
        outputStream.write(body.getContent());
        outputStream.flush();
    }
}
package server.response;

import java.io.IOException;
import java.io.OutputStream;

public class Forbidden extends Response {
    public Forbidden(String documentRoot) {
        super(documentRoot, 403, "Forbidden");
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        headers.set("Content-Type", "text/html");
        body.setContent("<h1>403 Forbidden</h1>".getBytes());

        outputStream.write((statusLine.toString() + "\r\n").getBytes("ISO-8859-1"));
        outputStream.write(headers.toByteArray());
        outputStream.write(body.getContent());
        outputStream.flush();
    }
}
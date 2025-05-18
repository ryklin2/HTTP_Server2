package server.response;

import java.io.IOException;
import java.io.OutputStream;

public class Unauthorized extends Response {
    public Unauthorized(String documentRoot) {
        super(documentRoot, 401, "Unauthorized");
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        headers.set("WWW-Authenticate", "Basic realm=\"667 Server\"");
        headers.set("Content-Type", "text/html");
        body.setContent("<h1>401 Unauthorized</h1>".getBytes());

        outputStream.write((statusLine.toString() + "\r\n").getBytes("ISO-8859-1"));
        outputStream.write(headers.toByteArray());
        outputStream.write(body.getContent());
        outputStream.flush();
    }
}
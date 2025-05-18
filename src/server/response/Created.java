package server.response;

import java.io.IOException;
import java.io.OutputStream;

public class Created extends Response {
    public Created(String documentRoot) {
        super(documentRoot, 201, "Created");
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        outputStream.write((statusLine.toString() + "\r\n").getBytes("ISO-8859-1"));
        outputStream.write(headers.toByteArray());
        outputStream.flush();
    }
}
package server.response;

import server.config.MimeTypes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.OutputStream;
import java.io.IOException;

public class Ok extends Response {
    private final Path filePath;

    public Ok(String documentRoot, Path filePath) {
        super(documentRoot, 200, "OK");
        this.filePath = filePath;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        byte[] content = Files.readAllBytes(filePath);
        String mimeType = MimeTypes.getMimeTypeFromExtension(getExtension(filePath));
        headers.set("Content-Type", mimeType != null ? mimeType : "application/octet-stream");
        headers.set("Content-Length", String.valueOf(content.length));
        body.setContent(content);

        outputStream.write((statusLine.toString() + "\r\n").getBytes("ISO-8859-1"));
        outputStream.write(headers.toByteArray());
        outputStream.write(body.getContent());
        outputStream.flush();
    }

    private String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }
}
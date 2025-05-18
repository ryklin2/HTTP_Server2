package server.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import server.err.HTTPException;

public class Body {
    private byte[] content;

    public Body() {
        this.content = new byte[0];
    }

    public void parse(InputStream input, int contentLength) throws IOException, HTTPException {
        if (contentLength < 0) {
            throw new HTTPException(400, "Invalid content length: " + contentLength);
        }
        if (contentLength == 0) {
            this.content = new byte[0];
            System.out.println("Body parsed (empty, length 0)");
            return;
        }

        // Create a byte array to store the content
        ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; // 8KB buffer for efficient reading
        int bytesRead;
        int totalBytesRead = 0;

        System.out.println("Starting to read body bytes");

        // Simple, reliable loop to read all the data
        while (totalBytesRead < contentLength) {
            bytesRead = input.read(buffer, 0, Math.min(buffer.length, contentLength - totalBytesRead));
            if (bytesRead == -1) {
                System.out.println("End of stream reached after reading " + totalBytesRead + " bytes");
                break; // End of stream
            }
            bodyBuffer.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
            System.out.println("Read " + bytesRead + " bytes, total now: " + totalBytesRead + " of " + contentLength);
        }

        this.content = bodyBuffer.toByteArray();
        System.out.println("Body parsing complete. Read " + totalBytesRead + " bytes. Content: '" +
                (this.content.length > 100 ? "[content too large to display]" : new String(this.content)) + "'");
    }

    public byte[] getContent() { return content; }
    public void setContent(byte[] inBody) {
        this.content = (inBody != null) ? inBody : new byte[0];
    }
    public int getLength() { return content.length; }
}
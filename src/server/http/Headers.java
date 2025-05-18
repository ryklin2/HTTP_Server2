package server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import server.err.HTTPException;

public class Headers {
    private HashMap<String, String> headerMap;

    public Headers() {
        this.headerMap = new HashMap<>();
    }

    public void parse(BufferedReader reader) throws IOException, HTTPException {
        String line;
        System.out.println("Reading headers:");
        while ((line = reader.readLine()) != null) {
            // An empty line indicates the end of headers
            if (line.trim().isEmpty()) {
                System.out.println("End of headers (empty line)");
                break;
            }

            System.out.println("Header line: " + line);
            String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                throw new HTTPException(400, "Invalid header line: " + line);
            }
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            headerMap.put(key, value);
            System.out.println("Added header: " + key + " = " + value);
        }
        System.out.println("Headers parsing complete");
    }

    public String get(String key) { return headerMap.get(key.toLowerCase()); }
    public void set(String key, String value) { headerMap.put(key.toLowerCase(), value); }
    public boolean contains(String key) { return headerMap.containsKey(key.toLowerCase()); }
    public String getAuthorizationHeader() { return headerMap.get("authorization"); }
    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();
        for (String key : headerMap.keySet()) {
            sb.append(key).append(": ").append(headerMap.get(key)).append("\r\n");
        }
        sb.append("\r\n");
        return sb.toString().getBytes();
    }
}
package server.config;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes {
    private static final Map<String, String> MIME_TYPES;

    static {
        MIME_TYPES = new HashMap<>();
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg"); // Corrected from "image/jpg" to "image/jpeg"
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
    }

    /*
    public static MimeTypes getDefault() {
        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeType("png", "image/png");
        mimeTypes.addMimeType("jpg", "image/jpeg"); // Corrected here too
        mimeTypes.addMimeType("jpeg", "image/jpeg");
        mimeTypes.addMimeType("txt", "text/plain");
        mimeTypes.addMimeType("html", "text/html");
        mimeTypes.addMimeType("htm", "text/html");
        mimeTypes.addMimeType("css", "text/css");
        mimeTypes.addMimeType("js", "application/javascript");
        mimeTypes.addMimeType("json", "application/json");
        // If additional mime types are needed, _valid_ mime types can be added here
        return mimeTypes;
    }
     */

    private Map<String, String> mimeTypes;

    public MimeTypes() {
        this.mimeTypes = new HashMap<>();
    }

    public static String getMimeTypeFromExtension(String extension) {
        return MIME_TYPES.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }

    public void addMimeType(String extension, String mimeType) {
        this.mimeTypes.put(extension, mimeType);
    }
}
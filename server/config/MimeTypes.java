package server.config;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes {

    public static MimeTypes getDefault() {
        MimeTypes mimeTypes = new MimeTypes();

        mimeTypes.addMimeType("png", "image/png");
        mimeTypes.addMimeType("jpg", "image/jpg");
        mimeTypes.addMimeType("jpeg", "image/jpg");
        mimeTypes.addMimeType("txt", "text/plain");
        mimeTypes.addMimeType("html", "text/html");
        mimeTypes.addMimeType("htm", "text/html");
        mimeTypes.addMimeType("js", "text/javascript");
        mimeTypes.addMimeType("json", "application/json");

        mimeTypes.addMimeType("css", "text/css");
        mimeTypes.addMimeType("gif", "image/gif");
        mimeTypes.addMimeType("svg", "image/svg+xml");
        mimeTypes.addMimeType("pdf", "application/pdf");

        return mimeTypes;
    }

    private Map<String, String> mimeTypes;

    public MimeTypes() {
        this.mimeTypes = new HashMap<>();
    }

    public String getMimeTypeFromExtension(String extension) {
        return this.mimeTypes.get(extension);
    }

    public void addMimeType(String extension, String mimeType) {
        this.mimeTypes.put(extension, mimeType);
    }
}
package server;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import server.config.MimeTypes;

public class FileHandler {
    private Path documentRoot;
    private BufferedReader source;

    public FileHandler(Path documentRoot) {
        this.documentRoot = documentRoot;
    }

    public FileHandler(String sourceFile) throws IOException {
        this(new BufferedReader(new FileReader(sourceFile)));
    }

    public FileHandler(BufferedReader reader) throws IOException {
        this.source = reader;
    }

    // gets the content of the file as a byte array 
    public byte[] getFile(String requestPath) throws IOException {
        Path filePath = documentRoot.resolve(requestPath.substring(1)); // remove the starting '/' in requestPath
        return Files.readAllBytes(filePath);
    }
    // method to check if a file exists at the requested path
    public boolean fileExists(String requestPath) {
        Path filePath = documentRoot.resolve(requestPath.substring(1)); 
        return Files.exists(filePath);
    }
    
    // get the size of the file in bytes
    public long getFileSize(String requestPath) throws IOException{
        Path filePath = documentRoot.resolve(requestPath.substring(1)); 
        return Files.size(filePath);
    }

    public String readLine() throws IOException {
        return source.readLine();
    }

    // write content to a file 
    public boolean writeFile(String requestPath, byte[] content) {
        try {
            // get the full file path 
            Path filePath = documentRoot.resolve(requestPath.substring(1));
            
            // get parent directories (if there's any)
            Path parentDir = filePath.getParent();
            if(parentDir != null) {
                Files.createDirectories(parentDir);
            }
            
            // CREATE = creates file if it doesn't exist, opens it for writing if it does
            // TRUNCATE_EXISTING = truncates file to zero bytes 
            Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); 
            return true;
        } catch(IOException err) {
            System.err.println("Internal server error: " + err.getMessage());
            return false;
        }
    }

    // delete a file
    public void deleteFile(String requestPath) throws IOException {
        Path filePath = documentRoot.resolve(requestPath.substring(1));
        Files.delete(filePath);
    }

    // get the MIME type of the file
    public String getMimeType(String requestPath, MimeTypes mimeTypes) {
        String extension = getFileExtension(requestPath);
        return mimeTypes.getMimeTypeFromExtension(extension);
    }

    // get file extension off of the request path
    public String getFileExtension(String requestPath) {
        int indexOfDot = requestPath.lastIndexOf('.');
        
        // if there is not '.', return empty string, else return file extension
        if(indexOfDot == -1) {
            return "";
        } else {
            return requestPath.substring(indexOfDot + 1);
        }
    }

    public String resolveFullPath(String path) {
        return documentRoot + path;
    }
}

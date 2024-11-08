package server;

import server.config.MimeTypes;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebServer implements AutoCloseable {

    private ServerSocket serverSocket;
    private int port;
    private Path documentRoot;
    private MimeTypes mimeTypes;

    public static void main(String[] args) throws NumberFormatException, Exception {
        if (args.length != 2) {
            System.err.println("usage: java WebServer <port number> <document root>");
            System.exit(1);
        }

        try (WebServer server = new WebServer(Integer.parseInt(args[0]), args[1])) {
            server.listen();
        }
    }

    public WebServer(int port, String documentRoot) {
        this.port = port;
        this.documentRoot = Paths.get(documentRoot);
        this.mimeTypes = MimeTypes.getDefault();

        try {
            Files.createDirectories(this.documentRoot);
        } catch(IOException err) {
            System.err.println(err.getMessage());
        }
    }

    /**
     * 
     * Example of mimeTypeFileContent: html htm text/html\npng image/png\njpg
     * image/jpeg\ngif image/gif\n
     */
    public WebServer(int port, String documentRoot, String mimeTypeFileContent) {
        MimeTypes mimeTypes = MimeTypes.getDefault();

        // Parse the mimeTypesFileContent and add the mime types to the mimeTypes object
        mimeTypeFileContent.lines().forEach(line -> {
            String[] parts = line.split("\\s+");

            for (int index = 0; index < parts.length - 1; index++) {
                mimeTypes.addMimeType(parts[index], parts[parts.length - 1]);
            }
        });

    }
    
    /**
     * After the webserver instance is constructed, this method will be
     * called to begin listening for requests
     */
    public void listen() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);

            while(!serverSocket.isClosed()) {
                // accept connections
                Socket clientSocket = serverSocket.accept();
                
                // handle request in new thread to allow for multiple clients 
                new Thread(new RequestHandler(clientSocket, documentRoot, mimeTypes)).start();
            } 
        } catch(IOException err) {
                System.err.println("Error occurred: " + err.getMessage());
            }
    }

    @Override
    public void close() throws Exception {
        this.serverSocket.close();
    }
}
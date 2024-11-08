package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;

import server.config.MimeTypes;
import server.http.Request;
import server.http.Response;

public class RequestHandler implements Runnable {
    private Socket clientSocket;
    private MimeTypes mimeTypes;
    private FileHandler fileHandler;

    public RequestHandler(Socket clientSocket, Path documentRoot, MimeTypes mimeTypes) {
        this.clientSocket = clientSocket;
        this.mimeTypes = mimeTypes;
        this.fileHandler = new FileHandler(documentRoot);
    }

    // if you want to test
    // make a .password file in the directory
    public boolean authenticate(Request request) throws IOException {
        String passwords = (new File(request.getPath()).getParent()) + "/.passwords";

        if (fileHandler.fileExists(passwords)) {
            String passwordHeader = request.getHeader("Authorization");

            String credentials_value = ":";

            if (passwordHeader == null)
            {
                Response response = new Response(401, "Unauthorized");
                response.addHeader("WWW-Authenticate", "Basic realm=\"667 Server\"");
                response.send(clientSocket);
                return false;
            }
            else 
            {
                credentials_value = new String(Base64.getDecoder().decode(passwordHeader.split(" ")[1].getBytes()), 
                    StandardCharsets.UTF_8);
            }

            if (credentials_value.equals(":")) {
                Response response = new Response(401, "Unauthorized");
                response.addHeader("WWW-Authenticate", "Basic realm=\"667 Server\"");
                response.send(clientSocket);
                return false;
            }
            
            String[] user_pass = credentials_value.split(":");

            String client = user_pass[0];
            String client_pass = user_pass[1];

            FileHandler fileIn = new FileHandler(fileHandler.resolveFullPath(passwords));
            String in;
            while ((in = fileIn.readLine()) != null) {
                String[] file_credentials = in.split(":");

                String file_user = file_credentials[0];
                String file_password = file_credentials[1];

                // compare password here
                // if we have a match return true
                if (client.equals(file_user) &&
                    client_pass.equals(file_password)) {
                        return true;
                }
            }
            new Response(403, "Forbidden").send(clientSocket);
            return false;
        }
        return true;
    }

    public void GET(Request request) throws IOException {
        if(fileHandler.fileExists(request.getPath())) {
            byte[] body = fileHandler.getFile(request.getPath());
            Response response = new Response(200, "Success", body);
            String contentType = fileHandler.getMimeType(request.getPath(), mimeTypes);
            
            response.addHeader("Content-Type", contentType);
            response.send(clientSocket);
        } else {
            byte[] body = "The requested resource was not found".getBytes();
            Response response = new Response(404, "Not found", body);
            response.send(clientSocket);
        }
    }

    public void PUT(Request request) throws IOException {
        if(fileHandler.writeFile(request.getPath(), request.getBody())) {
            byte[] body = "File written successfully".getBytes();
            Response response = new Response(201, "Created", body);
            response.send(clientSocket);
        } else {
            byte[] body = "Failed to write file".getBytes();
            Response response = new Response(500, "Internal Server Error", body);
            response.send(clientSocket);
        }
    }

    public void HEAD(Request request) throws IOException {
        if(fileHandler.fileExists(request.getPath())) {
            Response response = new Response(200, "Success");
            response.send(clientSocket);
        } else {
            Response response = new Response(404, "Not Found");
            response.send(clientSocket);
        }
    }

    public void DELETE(Request request) throws IOException {
        if (fileHandler.fileExists(request.getPath())) {
            try {
                fileHandler.deleteFile(request.getPath());
                new Response(204, "No Content").send(clientSocket);  
            } catch (Exception e) {
                new Response(500, "Internal Server Error").send(clientSocket);
            }
        }
        else {
            new Response(404, "Not Found").send(clientSocket);
        }
    }
    
    @Override 
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();

            Request request;
            try {
                request = new Request(inputStream);
            } catch(IllegalArgumentException err) {
                Response response = new Response(400, "Invalid Request");
                response.send(clientSocket);
                clientSocket.close();
                return;
            }

            if (authenticate(request)) {
                switch(request.getMethod().toUpperCase()) {
                    case "GET":
                        GET(request);
                        break;
                    case "PUT":
                        PUT(request);
                        break;
                    case "DELETE":
                        DELETE(request);
                        break;
                    case "HEAD":
                        HEAD(request);
                        break;
                    default:
                        System.out.println("Bad request");
                        new Response(400, "Bad Request").send(clientSocket);
                        break;
                }
            }
        } catch(IOException err) {
            try {
                new Response(500, "Internal Server Error").send(clientSocket);
            } catch (IOException e) {
                System.err.println("FATAL");
                return;
            }
        }
    }
}

package server.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Request {
    
    private String method;
    private String path;
    private String httpVersion;
    private Map<String, String> headers;
    private byte[] body;

    // construct the Request with the raw HTTP request (InputStream)
    public Request(InputStream inputStream) throws IOException{
        System.out.println("Initializing a new request");
        this.headers = new HashMap<>();
        parseRequest(inputStream);
    }
    
    private void parseRequest(InputStream inputStream) throws IOException {
        StringBuilder requestLine = new StringBuilder();
        StringBuilder headerLine = new StringBuilder();
        int prevChar = -1; // tracks the previous character 

        // read the request line
        while(true) {
            int currentChar = inputStream.read();
            if(currentChar == -1) {
                // end of stream
                throw new IllegalArgumentException("Request line is wrong");
            }
            requestLine.append((char)currentChar);

            // check if end of request line has been hit
            if(prevChar == '\r' && currentChar == '\n') {
                String reqLine = requestLine.toString().trim();
                parseRequestLine(reqLine);
                break;
            }
            prevChar = currentChar;
        }
        // read the headers
        prevChar = -1; // resetting for the headers
        while(true) {
            int currentChar = inputStream.read();
            if(currentChar == -1) {
                // end of the stream
                break;
            }
            headerLine.append((char)currentChar);
            
            // check for the end of line
            if(prevChar == '\r' && currentChar == '\n') {
                // if length is 2, then the previous line was empty (\r\n\r\n), end of headers
                if(headerLine.length() == 2) {
                    break; 
                }
                String header = headerLine.toString().trim();
                parseHeaderLine(header);
                headerLine.setLength(0); // clear StringBuilder for the next header 
            }
            prevChar = currentChar;
        }
        if(this.headers.isEmpty()) {
            throw new IllegalArgumentException("Headers are empty");
        }
        // read body if it exists
        if(headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            this.body = new byte[contentLength];

            // read body from input stream in chunks until entire body is read
            int totalBytesRead = 0;
            while(totalBytesRead < contentLength) {
                int bytesRead = inputStream.read(this.body, totalBytesRead, contentLength - totalBytesRead);
                if(bytesRead == -1) {
                    throw new IOException("Unexpected end of stream encountered when reading body");
                }
                totalBytesRead += bytesRead;
            }
        }
    }

    private void parseRequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        if(tokens.length < 3) {
            throw new IllegalArgumentException("Request line is incorrect");
        }
        this.method = tokens[0]; // GET
        this.path = tokens[1]; // /document.html
        this.httpVersion = tokens[2]; // HTTP/1.1

        if(!httpVersion.startsWith("HTTP/")) {
            throw new IllegalArgumentException("Invalid HTTP version: " + httpVersion);
        }

        if(method.isEmpty() || path.isEmpty()) {
            throw new IllegalArgumentException("Invalid method or path");
        }
    }

    private void parseHeaderLine(String headerLine) {
        int indexOfColon = headerLine.indexOf(":");
        if(indexOfColon == -1) {
            throw new IllegalArgumentException("Header is not formatted correctly");
        } 

        String key = headerLine.substring(0, indexOfColon);
        String value = headerLine.substring(indexOfColon + 2);

        if(key.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("Incorrect header key or value");
        }

        headers.put(key, value);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHeader(String key) {
        return headers.get(key);
    } 

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}

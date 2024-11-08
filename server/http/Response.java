package server.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;

public class Response {

    private int statusCode;
    private String reasonPhrase;
    private Map<String, String> headers;
    private byte[] body;

    public Response(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = new HashMap<>();
    }

    public Response(int statusCode, String reasonPhrase, byte[] body) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = new HashMap<>();
        this.body = body;
        addHeader("Content-Length", String.valueOf(body.length));
        addHeader("Content-Type", "text/plain");
        addHeader("Date", java.time.LocalDateTime.now().toString());
    }

    // adds headers to the response
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    // this sets the response body byte array
    public void setBody(byte[] body) {
        this.body = body;
    }

    // this sets the response body with String
    public void setBody(String body) {
        this.body = body.getBytes();
    }

    // sends the actual response to the client
    public void send(Socket clientSocket) throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        // status line
        writer.write("HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n");
        // iterate through all headers (if any) and add it to the print writer
        for(Map.Entry<String, String> header: headers.entrySet()) {
            writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
        }

        writer.write("\r\n"); // end of headers 
        writer.flush(); // flush headers to the output stream

        // add body if it exists
        if(body != null && body.length > 0) {
            out.write(body);
            out.flush();
        }

        writer.close(); // close after sending
    }
}

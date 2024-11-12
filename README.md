<h2>Simple HTTP Server</h2>
<p>This project is a basic HTTP server built from scratch in Java, capable of handling HTTP GET, PUT, DELETE, and HEAD requests. Designed to serve static content, it parses HTTP requests and generates responses in compliance with HTTP standards.</p>

<h3>Features</h3>
<ul>
  <li>Handles HTTP methods: <code>GET</code>, <code>PUT</code>, <code>DELETE</code>, and <code>HEAD</code>.</li>
  <li>Serves static files from a specified document root.</li>
  <li>Simple and efficient request parsing and response generation.</li>
</ul>

<h3>Getting Started</h3>
<p>1. <strong>Compile the server:</strong></p>
<pre><code>javac WebServer.java</code></pre>

<p>2. <strong>Run the Server:</strong></p>
<pre><code>java WebServer &lt;port number&gt; &lt;path to document root&gt;</code></pre>
<p>Replace <code>&lt;port number&gt;</code> with the desired port (e.g., 8080)</p>
<p>Replace <code>&lt;path to document root&gt;</code> with the absolute path to the directory containing the files to serve</p>

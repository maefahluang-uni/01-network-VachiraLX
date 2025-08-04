package th.mfu;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MockWebServer implements Runnable {

    private int port;
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public MockWebServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Mock Web Server running on port " + port + "...");

            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    // Create input and output streams for the client socket
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Read the request (headers only, simple version)
                    String line;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        System.out.println("[" + port + "] " + line);
                    }

                    // Send a basic HTTP response
                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            "<html><body>Hello, Web! on Port " + port + "</body></html>";

                    out.print(response);
                    out.flush();

                    // Close client socket
                    clientSocket.close();

                } catch (IOException e) {
                    System.err.println("[" + port + "] Error handling client: " + e.getMessage());
                }
            }

            System.out.println("Server on port " + port + " stopped.");
        } catch (IOException e) {
            System.err.println("Could not start server on port " + port + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Thread server1 = new Thread(new MockWebServer(8080));
        Thread server2 = new Thread(new MockWebServer(8081));

        server1.start();
        server2.start();

        System.out.println("Press Enter to stop the server...");
        try {
            System.in.read(); // Wait for Enter key

            running.set(false); // Signal servers to stop

            // Give some time for servers to shut down gracefully
            Thread.sleep(1000);

            System.exit(0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
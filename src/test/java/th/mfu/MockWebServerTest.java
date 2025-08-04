package th.mfu;

import org.junit.*;

import java.io.*;
import java.net.*;

import static org.junit.Assert.assertTrue;

public class MockWebServerTest {

    private static final int PORT = 8080;
    private Thread mockWebServer;

    @Before
    public void setUp() throws InterruptedException {
        // Start the mock web server
        mockWebServer = new Thread(new MockWebServer(PORT));
        mockWebServer.start();

        // Wait briefly to ensure server is up before test
        Thread.sleep(500);
    }

    @After
    public void tearDown() {
        // Stop server thread
        mockWebServer.interrupt();
    }

    @Test
    public void testMockWebServer() throws IOException {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send a GET request
            out.print("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");
            out.flush();

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }

            String response = responseBuilder.toString();

            // Check that response contains expected parts
            assertTrue(response.contains("HTTP/1.1 200 OK"));
            assertTrue(response.contains("Content-Type: text/html"));
            assertTrue(response.contains("<html><body>Hello, Web! on Port " + PORT + "</body></html>"));
        }
    }
}
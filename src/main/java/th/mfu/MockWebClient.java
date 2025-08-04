package th.mfu;

import java.io.*;
import java.net.*;

public class MockWebClient {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            // Create a socket to connect to localhost at port 8080
            socket = new Socket("localhost", 8080);

            // Create output stream to send request
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Create input stream to read response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send HTTP GET request
            String request = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
            out.print(request);
            out.flush();

            // Read and print the response
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Close socket and resources
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println("Error closing socket: " + ex.getMessage());
            }
        }
    }
}
package p2pchatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerToPeerChat {
    public static void main(String[] args) {
        // Check if the program is run as a server or client
        if (args.length == 1 && args[0].equalsIgnoreCase("server")) {
            startServer();
        } else if (args.length == 3 && args[0].equalsIgnoreCase("client")) {
            String peerIp = args[1];
            int peerPort = Integer.parseInt(args[2]);
            startClient(peerIp, peerPort);
        } else {
            System.out.println("Usage: java PeerToPeerChat server");
            System.out.println("   or: java PeerToPeerChat client <peer-ip> <peer-port>");
        }
    }

    private static void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(0); // 0 for a randomly assigned available port

            System.out.println("Server started. Your IP address: " + serverSocket.getInetAddress());
            System.out.println("Your port number: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress());

                // Handle the client connection in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startClient(String peerIp, int peerPort) {
        try {
            Socket socket = new Socket(peerIp, peerPort);
            System.out.println("Connected to the peer.");

            // Implement the chat logic here
            // You can use input/output streams to send and receive messages
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println("Type your messages. Type 'exit' to end the chat.");

                // Thread to handle incoming messages from the connected peer
                new Thread(() -> {
                    try (BufferedReader peerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String receivedMessage;
                        while ((receivedMessage = peerReader.readLine()) != null) {
                            System.out.println("Received from " + socket.getInetAddress() + ": " + receivedMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                // Send user input to the connected peer
                String userInput;
                while (!(userInput = reader.readLine()).equalsIgnoreCase("exit")) {
                    writer.println(userInput);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Chat ended. Closing connection.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String receivedMessage;
            while ((receivedMessage = reader.readLine()) != null) {
                System.out.println("Received from " + clientSocket.getInetAddress() + ": " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

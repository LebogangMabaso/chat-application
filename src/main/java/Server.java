import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 5001;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Starting Chat Server on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running. Waiting for clients to connect...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public static void broadcastMessage(Message message, ClientHandler excludeClient) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        System.out.println("Client disconnected. Total clients: " + clientHandlers.size());
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());

                clientName = (String) input.readObject();
                System.out.println(clientName + " has joined the chat!");

                sendMessage(new Message("Server", "Welcome to the chat, " + clientName + "!"));

                broadcastMessage(new Message("Server", clientName + " has joined the chat"), this);

                while (true) {
                    Message message = (Message) input.readObject();
                    System.out.println("Received from " + clientName + ": " + message.getContent());

                    broadcastMessage(message, this);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(clientName + " disconnected: " + e.getMessage());
            } finally {
                try {
                    removeClient(this);
                    socket.close();

                    broadcastMessage(new Message("Server", clientName + " has left the chat"), this);
                } catch (IOException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }

        public void sendMessage(Message message) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                System.out.println("Error sending message to " + clientName + ": " + e.getMessage());
            }
        }
    }
}
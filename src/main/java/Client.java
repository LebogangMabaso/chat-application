import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private String hostname;
    private int port;
    private String userName;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();

        System.out.print("Enter server IP (localhost for same computer): ");
        String hostname = scanner.nextLine();

        if (hostname.isEmpty()) {
            hostname = "localhost";
        }

        Client client = new Client(hostname, 5001);
        client.userName = userName;
        client.execute();
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected to chat server");

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(userName);
            output.flush();

            new Thread(new ReadThread(input)).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                output.writeObject(new Message(userName, message));
                output.flush();
            }

            scanner.close();
            socket.close();
            System.out.println("Disconnected from server");

        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }

    class ReadThread implements Runnable {
        private ObjectInputStream input;

        public ReadThread(ObjectInputStream input) {
            this.input = input;
        }

        public void run() {
            try {
                while (true) {
                    Message message = (Message) input.readObject();
                    System.out.println(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Disconnected from server");
            }
        }
    }
}
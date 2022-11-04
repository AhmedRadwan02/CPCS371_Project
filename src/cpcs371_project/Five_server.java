package cpcs371_project;

import java.net.*;
import java.io.*;

public class Five_server {

    private Socket socket;
    private ServerSocket server;
    private DataInputStream clientInputStream;
    private DataOutputStream clientOutputStream;
    private int port;

    private void initServer() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started");
        System.out.println("Waiting for a client ...");

    }

    private void listenForClients() throws IOException {
        socket = server.accept();
        clientInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        clientOutputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("A client has connected");
    }

    public Five_server(int port) {
        try {
            this.port = port;
            initServer();
            listenForClients();
            readClientInput();
            System.out.println("Closing connection");
            closeConnections();
        } catch (BindException error) {
            System.err.println("A server is already running on the same port!");
            System.exit(2);
        } catch (SocketException error) {
            System.err.println("The client has disconnected");
            System.exit(0);
        } catch (IOException error) {
            System.err.println(error);
            System.err.println("Something went wrong with IO");
            System.exit(1);
        }
    }

    private void closeConnections() throws IOException {
        socket.close();
        clientInputStream.close();
        clientOutputStream.close();
    }

    private void readClientInput() throws IOException {
        // reads message from client until "n" is sent
        String clientInput = "";
        while (!clientInput.equals("n")) {
            String streamInput = clientInputStream.readUTF();
            if (streamInput.equals("n")) {
                break;
            }
            String[] splitClientInput = streamInput.split(",");
            int numberOfOccurrences = findNumberOfOccurrences(splitClientInput[0], splitClientInput[1].charAt(0));
            sendClientTheResult(numberOfOccurrences);
        }
        clientOutputStream.writeUTF("Thank you!");
    }

    private void sendClientTheResult(int numberOfOccurrences) throws IOException {
        clientOutputStream.writeInt(numberOfOccurrences);
    }

    private int findNumberOfOccurrences(String string, char characterToMatch) {
        int length = string.length();
        int occurrence = 0;
        for (int i = 0; i < length; i++) {
            if (string.charAt(i) == characterToMatch) {
                occurrence++;
            }
        }
        return occurrence;
    }

    public static void main(String args[]) {
        Five_server server = new Five_server(5000);
    }
}

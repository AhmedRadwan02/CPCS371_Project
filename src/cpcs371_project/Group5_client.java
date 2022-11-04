package cpcs371_project;

import java.net.*;
import java.io.*;

public class Group5_client {

    private Socket socket;
    private DataInputStream userInput;
    private DataInputStream serverOutput;
    private DataOutputStream serverInput;
    private String address;
    private int port;

    public Group5_client(String address, int port) {
        try {
            this.address = address;
            this.port = port;
            connect();
        } catch (UnknownHostException error) {
            System.err.println(error);
            System.err.println("The connection cannot be established.\nThe IP/port you have provided is not correct. The connection could not be ");
            System.exit(1);
        } catch (IOException error) {
            System.err.println(error);
            System.err.println("The connection cannot be established.\nThe server you're trying to connect to might not be avaliable");
            System.exit(2);
        }
        readUserInput();
        // close the connection
        closeConnection();
    }

    private void connect() throws IOException {
        socket = new Socket(this.address, this.port);
        if (socket.isConnected()) {
            userInput = new DataInputStream(System.in);
            serverOutput = new DataInputStream(socket.getInputStream());
            serverInput = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to the server.");
        }
    }

    private void readUserInput() {
        String charToSearch;
        String line = "";
        char repeat = 'y';
        while (repeat != 'n' && !socket.isClosed()) {
            try {
                charToSearch = readChar();
                line = readSentence();
                serverInput.writeUTF(String.format("%s,%s", line, charToSearch));
                readServerOutput();
                if ( continueReading().equals("n")) {
                    waitForConnectionToClose();
                    repeat = 'n';
                }
            } catch (SocketException error) {
                System.err.println("The connection was shutdown from the server side.");
                System.exit(2);
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    private void waitForConnectionToClose() throws IOException {
        serverInput.writeUTF("n");
        System.out.println(serverOutput.readUTF());
    }

    private void readServerOutput() throws IOException {
        System.out.printf("The number of Occurrences are: %d\n", serverOutput.readInt());
    }

    private String continueReading() throws IOException {
        String choice = "";
        while (true) {
            System.out.printf("Want to repeat?(Y/N): ");
            choice = userInput.readLine().toLowerCase();
            if (choice.equals("y") || choice.equals("n")) {
                return choice;
            }
            System.out.println("Incorrect input, enter either (Y/N)");
        }
    }

    private String readChar() throws IOException {
        while (true) {
            System.out.printf("Enter a Character to be searched: ");
            String line = userInput.readLine();
            if (line.length() > 1 || line.equals("")) {
                System.err.println("You've entered more than one character!\nPlease try again.");
                continue;
            }
            return line.substring(0, 1);
        }
    }

    private String readSentence() throws IOException {
        while (true) {
            System.out.printf("Enter a String: ");
            String line = userInput.readLine();
            if (line.length() == 0) {
                System.err.println("You did not enter any string!\nPlease try again.");
                continue;
            }
            return line;
        }
    }

    private void closeConnection() {
        try {
            userInput.close();
            serverOutput.close();
            socket.close();
        } catch (IOException error) {
            System.out.println(error);
        }
    }

    public static void main(String args[]) {
        Group5_client client = new Group5_client("127.0.0.1", 5000);
    }

}

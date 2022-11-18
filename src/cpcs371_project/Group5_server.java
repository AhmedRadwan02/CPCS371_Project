package cpcs371_project;

import java.net.*;
import java.io.*;

public class Group5_server {

    private Socket socket;
    private ServerSocket server;
    private DataInputStream clientInputStream;
    private DataOutputStream clientOutputStream;
    private int port;

    private void initServer() throws IOException {
    	// create server socket
        server = new ServerSocket(port);
        // set time out to kill connection, will close after 50 seconds
    	try {
    	server.setSoTimeout(50000);
    	}catch(IOException i) {
    		System.err.println("There was no clinets after 50s, server closed");
    	}
        System.out.println("Server started");
        System.out.println("Waiting for a client ...");

    }
    // keep listening for clients to connect, until someone starts a connection
    private void listenForClients() throws IOException {
        socket = server.accept();
        clientInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        clientOutputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("A client has connected");
    }

     public Group5_server(int port) {
        try {
            this.port = port;
            // start server
            initServer();
            // listen for clients
            listenForClients();
            // read the input
            readClientInput();
            System.out.println("Closing connection");
            closeConnections();
        } catch (BindException error) {
            System.err.println("A server is already running on the same port!");
            System.exit(2);
        } catch (SocketException error) {
            System.err.println("The client has disconnected");
            System.exit(0);
        } catch(SocketTimeoutException error) {
            System.err.println("There was no answer after 50s, try again");
        } catch (IOException error) {
            System.err.println(error);
            System.err.println("Something went wrong with IO");
            System.exit(1);
        }
    } 
    // close the socket and connection
    private void closeConnections() throws IOException {
        socket.close();
        clientInputStream.close();
        clientOutputStream.close();
    }

    private void readClientInput() throws IOException {
        // reads message from client until "n" is sent
    	// set time out to 50 second, if no input received stop server
    	socket.setSoTimeout(50000);
    	
        String clientInput = "";
        // keep the loop until the user input n to stop the repeat
        while (!clientInput.equals("n")) {
        	// read client input
            String streamInput = clientInputStream.readUTF();
            // if n stop the loop
            if (streamInput.equals("n")) {
                break;
            }
            // split the string 
            String[] splitClientInput = splitInput(streamInput);
            int numberOfOccurrences = findNumberOfOccurrences(splitClientInput[0], splitClientInput[1].charAt(0));
            sendClientTheResult(numberOfOccurrences);
        }
        clientOutputStream.writeUTF("Thank you!");
    }
    // the string line will be from 0 to the before the last
    // the character to search for will be the last character in inputed string
    private String[] splitInput(String input){
        String line = input.substring(0, input.length()-2);
        String charToSearch = input.substring(input.length()-1);
        return new String[]{line, charToSearch};
    }
    // send result 
    private void sendClientTheResult(int numberOfOccurrences) throws IOException {
        clientOutputStream.writeInt(numberOfOccurrences);
    }
    // find number of occurrences O(n)
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
        Group5_server server = new Group5_server(5000);
    }
}

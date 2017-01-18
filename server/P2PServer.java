import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class P2PServer {
   
    // Thread for running the server
    public P2PServerThread serverThread;

    // Server socket
    public ServerSocket server;

    // Lists for chat
    public ArrayList<Socket> sockets = new ArrayList<Socket>();
    public ArrayList<String> clientsInChat = new ArrayList<String>();

    // Sockets for sending and recieving
    public Socket recSocket;
    public Socket fileTransSocket;
    public Socket connectionSocket;

    // Lists for file sharing sockets
    public ArrayList<Socket> clientFileSharingSocket = new ArrayList<Socket>();
    public ArrayList<String> clientFileSharingUsername = new ArrayList<String>();
    
    // Constructor for this class to prevent compilation errors
    public P2PServer() {}
    
    public void addNewSocketToList(Socket socket){
        try {
            sockets.add(socket);
            System.out.println("Added new socket: "+socket.getInetAddress()+socket.getPort());
        } catch (Exception e) { 
            System.err.println("Error: Unable to add new socket to list\n"+ e.getMessage()); 
        }
    }

    // Adds the new clients to the list of active clients
    public void addNewClientToList(String newClient){
        try {
            clientsInChat.add(newClient);
            System.out.println("Added new client: "+newClient);
        } catch (Exception e) { 
            System.err.println("Error: Unable to add client to list.\n"+ e.getMessage()); 
        }
    }

    // Adds the client to the filesharing list when they are ready for a file transfer
    public void setClientFileSharingUsername(String user){
        try {
            clientFileSharingUsername.add(user);
        } catch (Exception e) {
            System.err.println("Error: Unable to add the client to the file sharing list\n"+e.getMessage());
        }
    }
    
    
    public void setClientFileSharingSocket(Socket soc){
        try {
            clientFileSharingSocket.add(soc);
        } catch (Exception e) { 
            System.out.println("Error: Unable to add the client's socket to the file sharing sockets list"+e.getMessage());
        }
    }
    
    // Get client socket from the list
    public Socket getClientList(String client){
        System.out.println("Getting file transfer socket.");
        fileTransSocket = null;
        for(int i = 0; i < clientsInChat.size(); i++){
            if(clientsInChat.get(i).equals(client)){
                fileTransSocket = sockets.get(i);
                return fileTransSocket;
            }
        }
        return fileTransSocket; // Will return null if there is no such client
    }
    
    // Remove a client from the lists forr chat.  Used for disconnecting clients.
    public void removeFromTheList(String client){
        System.out.println("Starting removal of client from lists.");
        try {
            for(int i = 0; i < clientsInChat.size(); i++){
                if(clientsInChat.get(i).equals(client)){ // Check for name
                    // Remove from each list
                    clientsInChat.remove(i);
                    sockets.remove(i);
                    System.out.println("Removed '"+ client+"' from the lists.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: Could not remove the socket/client from the lists\n"+ e.getMessage());
        }
    }
    
    // Get the connection socket to be for file transfer corresponding to the client
    public Socket getTransSocket(String user){
        System.out.println("Getting transfer socket");
        fileTransSocket = null;
        for(int i=0; i < clientFileSharingUsername.size(); i++){
            if(clientFileSharingUsername.get(i).equals(user)){
                fileTransSocket = clientFileSharingSocket.get(i);
                return fileTransSocket;
            }
        }
        return fileTransSocket;
    }

    public Socket getConnectionSocket(String user){
        System.out.println("Getting Active Clients");
        connectionSocket = null;
        for(int i=0; i < clientsInChat.size(); i++){
            if(clientsInChat.get(i).equals(user)){
                connectionSocket = sockets.get(i);
                return connectionSocket;
            }
        }
        return connectionSocket;
    }
    
    // Remove the client from all lists used for file sharing
    public void endFileShare(String user){
        System.out.println("Ending file share.");
        System.out.println("Starting removal");
            try {
                // Get the socket that the client is using for file transfer
                recSocket = getTransSocket(user);

                // If the socket is still open then close it 
                if(recSocket != null)
                    recSocket.close();
                
                // After the socket is closed remove it from all of the lists
                System.out.println("Removing client from file sharing client list.");

                // This line is meant mostly for debbuging
                System.out.println("File Share "+clientFileSharingUsername.toString());

                // Remove all usernames for filesharing
                clientFileSharingUsername.clear();

                System.out.println("Removing client from socket list.");

                // This line is meant mostly for debbuging
                System.out.println("File Socket "+clientFileSharingSocket.toString());

                // Remove all client sockets for filesharing
                clientFileSharingSocket.clear();
            } catch (IOException e) {
                System.out.println("Error: Unable to remove the client from the lists."+ e.getMessage());
            }
    }
    
    
    public static void main(String args[]) {
        // Make a new instance off the server
        P2PServer main = new P2PServer();

        // Get the port 
        int port = Integer.parseInt(args[0]);

        // Create and start a new thread for the server
        Thread t = new Thread(new P2PServerThread(port, main));
        t.start();
        
        // Create and start a new server for the list of online connections
        Thread ol = new Thread(new ClientTrackerThread(main));
        ol.start();
    }
}

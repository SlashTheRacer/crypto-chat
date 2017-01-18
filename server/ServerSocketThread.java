import java.io.*;
import java.net.Socket;
import java.util.*;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class ServerSocketThread implements Runnable {
    
    private String message;
    private ArrayList<String> tokenListSend, tokenListMeta;
    private String messageBody;
    public Socket socket;
    public P2PServer server;
    public DataInputStream dis;
    private String client, fileShareUser;
    private final int BUFF = 1024;
    
    public ServerSocketThread(Socket socket, P2PServer server){
        this.server = server;
        this.socket = socket;
        createDataStream(socket);
    }

    private void createDataStream(Socket socket){
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error: Unable to create the stream."+ e.getMessage());
        }
    }
    
    /*   This method will get the client socket in client socket list then stablish a connection    */
    private void createConnection(String recipient, String sender, String filename){
        try {
            //Socket s = ;
            if(server.getClientList(recipient) != null){ // Client was exists

                // Create the stream for that socket on the receiver 
                DataOutputStream socketOutputDataStream = new DataOutputStream(server.getClientList(recipient).getOutputStream());

                // Notiffy that the connection has been made right
                System.out.println("Connection has been made");

                // Construct the message to be written to the socket
                message = "file_metaInf "+sender+" "+recipient +" "+filename;
                socketOutputDataStream.writeUTF(message);

                // Show the message that was written to the socket
                System.out.println("Connection made and sent message: "+message);
            }else{
                DataOutputStream errorDataStream = new DataOutputStream(socket.getOutputStream());
                errorDataStream.writeUTF("send_err "+ "Client '"+recipient+"' was not found.");
                System.err.println("Could not find or make a connection to the client '"+recipient+"'");
            }
        } catch (IOException e) {
            System.err.println("Was unable to connect to the client due to an error."+e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            while(true){
                StringTokenizer tokens = new StringTokenizer(dis.readUTF());
                switch(tokens.nextToken()){
                    case "join_chat":
                        String newClient = tokens.nextToken();
                        System.out.println("Getting new client...");

                        // Add to the active clients list
                        server.addNewClientToList(newClient);
                        System.out.println("User Name: "+newClient);

                        // Get the socket and and it to the socket list
                        server.addNewSocketToList(socket);
                        System.out.println("Socket Port: "+socket.getPort());

                        System.out.println(newClient+" has connected.");
                        
                        // Let all clients know that a user has connected
                        String newClientContent = "SERVER '"+newClient+"' has joined chat.";

                        for(String client : server.clientsInChat){
                            if(!client.equals(newClient)){
                                try {
                                    // Get the index of the specific client
                                    int index = server.clientsInChat.indexOf(client);

                                    // Get the socket information about a client and use that to transfer the message to them
                                    DataOutputStream messageDataStream = 
                                        new DataOutputStream(server.sockets.get(index).getOutputStream());

                                    // Write the message from the chat to the specific client
                                    messageDataStream.writeUTF("mes "+newClientContent);
                                } catch (IOException e) {
                                    System.err.println("Error: Unable to send the message to "+
                                        "the rest of the clients.\n"+ e.getMessage());
                                }
                            }
                        }
                        break;
                    case "send_mes":
                        String sender = tokens.nextToken();
                        StringBuilder messageToSend = new StringBuilder();

                        // Get the whole message from the tokens
                        while(tokens.hasMoreTokens()){
                            messageToSend.append(" "+tokens.nextToken());
                        }

                        // Createe the messaage body and the message to be sent to the clients
                        messageBody = messageToSend.toString();
                        String content = sender+" "+messageToSend.toString();

                        // For all but the sender write the message to them
                        for(String client : server.clientsInChat){
                            if(!client.equals(sender)){
                                try {
                                    // Get the index of the specific client
                                    int index = server.clientsInChat.indexOf(client);

                                    // Get the socket information about a client and use that to transfer the message to them
                                    DataOutputStream messageDataStream = 
                                        new DataOutputStream(server.sockets.get(index).getOutputStream());

                                    // Write the message from the chat to the specific client
                                    messageDataStream.writeUTF("mes "+content);
                                } catch (IOException e) {
                                    System.err.println("Error: Unable to send the message to "+
                                        "the rest of the clients.\n"+ e.getMessage());
                                }
                            }
                        }

                        // Write message that was sent to the terminal on the server.
                        System.out.println(sender+": "+messageBody);
                        break;
                    case "socket_con":
                        System.out.println("Connecting for filesharing.");

                        // Get the username for the connection
                        fileShareUser = tokens.nextToken();

                        // Add the username and the active sockets to the filesharing lists
                        server.setClientFileSharingUsername(fileShareUser);
                        server.setClientFileSharingSocket(socket);

                        // Notify that the connection has been made OK to the socket
                        System.out.println("Conection made to: "+fileShareUser+" @ "
                            +socket.getInetAddress()+":"+socket.getPort());
                        break;
                    case "send":
                        // Add the tokens to an array list for easier managment
                        tokenListSend = new ArrayList<String>();
                        while(tokens.hasMoreTokens()){
                            tokenListSend.add(tokens.nextToken());
                        }

                        System.out.println("**********  FILE SHARING **********");
                        System.out.println("From: "+ tokenListSend.get(2));
                        System.out.println("To: "+ tokenListSend.get(1));
                        System.out.println("***********************************");

                        // Make sure that the client recieving the file has a socket that exists
                        if(server.getTransSocket(tokenListSend.get(1)) != null){ 
                            try {
                                System.out.println("FILE SENDING NOW");
                                InputStream input = socket.getInputStream();
                                OutputStream sendFile = server.getTransSocket(tokenListSend.get(1)).getOutputStream();
                                byte[] buffer = new byte[BUFF];
                                int in;

                                // Create the body of the message
                                messageBody = tokenListSend.get(0) +" "+ tokenListSend.get(2);

                                // Create the stream to send the message
                                DataOutputStream dataStreamSend = 
                                    new DataOutputStream(server.getTransSocket(tokenListSend.get(1)).getOutputStream());
                                
                                // Write that message to the socket stream
                                dataStreamSend.writeUTF("send "+messageBody);

                                // Read in the file stream from the socket
                                while((in = input.read(buffer)) > 0){
                                    sendFile.write(buffer, 0, in);
                                }

                                // Cleanup
                                sendFile.flush();
                                sendFile.close();

                                // End file sharing for the select clients
                                server.endFileShare(tokenListSend.get(1));
                                server.endFileShare(tokenListSend.get(2));
                            } catch (IOException e) {
                                System.err.println("Error: Unable to send the file\n"+e.getMessage());
                            }
                        }else{ 
                            // Close filesharing on error
                            server.endFileShare(tokenListSend.get(2));

                            // Create and write to the stream that produces the error message
                            DataOutputStream errorDataStream = new DataOutputStream(socket.getOutputStream());
                            errorDataStream.writeUTF("send_err "+ "Client '"+tokenListSend.get(1)+"' was not found, File Sharing will exit.");
                        }                        
                        break;  
                    case "send_resp":
                        StringBuilder responseMessage = new StringBuilder();
                        String recipient = tokens.nextToken();

                        // Create the message to be sent as the response 
                        while(tokens.hasMoreTokens()){
                            responseMessage.append(" "+tokens.nextToken());
                        }

                        // Attempt to send a response back to the sender 
                        try {
                            DataOutputStream responseDataSTream = new DataOutputStream(server.getTransSocket(recipient).getOutputStream());
                            message = "send_resp" +" "+ recipient +" "+ responseMessage;
                            responseDataSTream.writeUTF(message);
                        } catch (IOException e) {
                            System.err.println("Error: Was unable to send the ressponse back to the sender.\n"+ e.getMessage());
                        }
                        break;
                    case "send_metaInf":                      
                        try {
                            tokenListMeta = new ArrayList<String>();

                            // Tokenize and store the message
                            while(tokens.hasMoreTokens()){
                                tokenListMeta.add(tokens.nextToken());
                            }

                            // Create a connection based on the info recieved from the message
                            this.createConnection(tokenListMeta.get(1), tokenListMeta.get(0), tokenListMeta.get(2));
                        } catch (Exception e) {
                            System.err.println("Error: Was unable to send the information about the transfer.\n"
                                +e.getMessage());
                        }
                        break;
                    case "sending_err":  
                        // Tokenize recipient of the error
                        StringBuilder errorMessage = new StringBuilder();
                        String clientInError = tokens.nextToken();

                        // Create the error message from the message
                        while(tokens.hasMoreTokens()){
                            errorMessage.append(" "+tokens.nextToken());
                        }

                        // Try to create the error message and send it to the client
                        try {
                            DataOutputStream errorDataStream = new DataOutputStream(server.getTransSocket(clientInError).getOutputStream());
                            message = "receive_err "+ errorMessage.toString();
                            errorDataStream.writeUTF(message);
                        } catch (IOException e) {
                            System.err.println("Error: Unable to generate the error message.\n"
                                + e.getMessage());
                        }
                        break;
                    case "sending_acc": 
                        String acceptRecipient = tokens.nextToken();
                        StringBuilder acceptMessage = new StringBuilder();

                        while(tokens.hasMoreTokens()){
                            acceptMessage.append(" "+tokens.nextToken());
                        }

                        // Try to send the message that the file transfer has been accepted
                        try {
                            DataOutputStream acceptDataStream = new DataOutputStream(server.getTransSocket(acceptRecipient).getOutputStream());
                            acceptDataStream.writeUTF("receive_acc "+acceptMessage.toString());
                        } catch (IOException e) {
                            System.err.println("Error: Unable to send message that client accepted file transfer.\n"+ e.getMessage());
                        }
                        break;
                    case "client_discon": // Remove client on disconnect
                        String client = tokens.nextToken();
                        System.out.println(client);
                        System.out.println("Removing: "+client);
                        server.removeFromTheList(client);
                        break;
                    case "client_online":
                        String requestingClient = tokens.nextToken();
                        StringBuilder clients = new StringBuilder();
                        DataOutputStream onlineClientsStream = new DataOutputStream(server.getConnectionSocket(requestingClient).getOutputStream());

                        // Get all active clients
                        for(String onlineClient : server.clientsInChat){
                            clients.append(" "+onlineClient);
                        }

                        // Send the active clients back to the requestor
                        onlineClientsStream.writeUTF("online_resp "+clients);
                        break;
                    default: 
                        System.out.println("MESSAGE HEADER NOT KNOWN");
                    break;
                }
            }
        } catch (IOException e) {
            server.removeFromTheList(client);
            if(fileShareUser != null){
                server.endFileShare(fileShareUser);
            }
        }
    }
    
}

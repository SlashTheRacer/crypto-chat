import java.io.*;
import java.util.*;
import java.net.Socket;
import javax.swing.JOptionPane;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class P2PThread implements Runnable{
    
    protected Socket socket;
    private DataInputStream dataStreamIn;
    private DataOutputStream dataStream;
    protected P2PClient client;
    protected StringTokenizer tokens;
    
    public P2PThread(Socket socket, P2PClient client){
        this.client = client;
        this.socket = socket;
        try {
            dataStreamIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error: "+e.getMessage());
        }
    }

    @Override
    public void run() { // Opperation of the thread during the app execution
        try {
            while(!Thread.currentThread().isInterrupted()){
                tokens = new StringTokenizer(dataStreamIn.readUTF());

                // Based on what the message from the socket is do something
                switch(tokens.nextToken()){
                    case "mes": // If there is a message sent by one of the peers
                        String messSender = tokens.nextToken();
                        StringBuilder mess = new StringBuilder();
                        while(tokens.hasMoreTokens()){
                            mess.append(" "+tokens.nextToken());
                        }
                        client.getMess(mess.toString(), messSender);
                        break;
                    case "onl": // Add to the list of online clients
                        ArrayList<String> online = new ArrayList<String>();
                        while(tokens.hasMoreTokens()){ // Read tokenized message
                            String tokenList = tokens.nextToken(); 
                            if(!tokenList.equalsIgnoreCase(client.user)){
                                online.add(tokenList);
                            }
                        }
                        break;
                    case "file_metaInf":  // Notify peer of a file being sent to them
                        String[] tokenArr = new String[3];
                        int i = 0;
                        while(tokens.hasMoreTokens()){
                            tokenArr[i] = tokens.nextToken();
                            i++;
                        }
                        if(JOptionPane.showConfirmDialog(client, "From: "+tokenArr[0]
                                +"\nFile: "+tokenArr[2]+"\nBegin file transfer?") == 0){ // client accepted the request, then inform the sender to send the file now
                            try {
                                //Specify which directory to write the file to
                                client.useDirPath(); 

                                // Create the needed stream, connection, etc. to begin the file transfer if the peer approved
                                dataStream = new DataOutputStream(socket.getOutputStream());
                                String format = "sending_acc "+tokenArr[0]+" accepted";
                                Socket fileTransSocket = new Socket(client.getHostAddr(), client.getHostPort());
                                DataOutputStream fileDataStream = new DataOutputStream(fileTransSocket.getOutputStream());

                                // Write commands to the socket
                                dataStream.writeUTF(format);
                                fileDataStream.writeUTF("socket_con "+ client.getUser());
                                
                                // Start the thread to run the process
                                Thread rft = new Thread(new RecThread(fileTransSocket, client));
                                rft.start();
                            } catch (IOException e) { // IOException thrown
                                System.err.println("Error: "+e.getMessage());
                            }
                        } else { 
                            try {
                                // Still create the data stream to reply to the sender if denied 
                                dataStream = new DataOutputStream(socket.getOutputStream());

                                // Write command to the socket
                                dataStream.writeUTF("sending_err "+tokenArr[0]+" peer '"+tokenArr[1]+"' rejected your file.");
                            } catch (IOException e) {
                                System.err.println("Error: "+e.getMessage());
                            }
                        }                       
                        break;   
                     case "online_resp":
                        // Get and tokenize the list of active clients from the server
                        StringBuilder activeClients = new StringBuilder();
                        while(tokens.hasMoreTokens()){
                            activeClients.append(tokens.nextToken()+"\n");
                        }
                        JOptionPane.showMessageDialog(client, activeClients.toString());
                        break;
                    default: 
                        client.getMess("Unknown Command '"+tokens.nextToken()+"'", "Error: ");
                    break;
                }
            }
        } catch(IOException e){
            client.getMess("Connection lost", "Error: ");
        }
    }
}

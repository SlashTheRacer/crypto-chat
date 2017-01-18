import java.io.*;
import java.net.Socket;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class ClientTrackerThread implements Runnable {
    
    public P2PServer main;
    private String message;
    
    public ClientTrackerThread(P2PServer main){
        this.main = main;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()){
                message = "";

                // Create a list of the online clients 
                for(String client : main.clientsInChat){
                    message += " "+client;
                }
                
                // Notify all other clients of who is online
                for(Socket socket : main.sockets){
                    
                    // Create a datastream for sending the other online clients to the client
                    DataOutputStream dataStream = new DataOutputStream(socket.getOutputStream());

                    if(message.length() != 0)
                        dataStream.writeUTF("onl "+ message);
                }
                Thread.sleep(1900);
            }
        } catch(InterruptedException e){
            System.out.println("Error: Thread was interrupted.\n"+e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: Error updating list.\n"+e.getMessage());
        }
    } 
}

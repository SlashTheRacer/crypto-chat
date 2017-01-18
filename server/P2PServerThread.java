import java.io.IOException;
import java.net.*;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class P2PServerThread implements Runnable {
    
    private ServerSocket server;
    private P2PServer main;
    
    public P2PServerThread(int port, P2PServer main){
        try {
            this.main = main;
            server = new ServerSocket(port);
            System.out.println("*********************************");
            System.out.println("*           P2P SERVER          *");
            System.out.println("*              v1.0             *");
            System.out.println("*  Jacob Reed / Justin Jarrard  *");
            System.out.println("*********************************");
            System.out.println("THREAD FOR SERVER INITIALIZED");
            System.out.println("THREAD STARTED");
            System.out.println("*** SERVER  READY ***");
        } 
        catch (IOException e) { 
            System.out.println("Error: Unable to start the server.\n"
                +e.getMessage()); 
        }
    }

    @Override
    public void run() {
        try {
            while(true){
                // Create and start the necessary threads for the server
                ServerSocketThread socketThread = new ServerSocketThread(server.accept(), main);
                Thread st = new Thread(socketThread);
                st.start();
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to start the server.\n"
                + e.getMessage());
        }
    }
}

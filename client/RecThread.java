import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import javax.swing.*;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class RecThread implements Runnable {
    
    private Socket socket;
    private OutputStream strOut;
    private String filePATH, recipient;
    private DataInputStream dataStreamIn;
    private InputStream strIn;
    private StringTokenizer tokens;
    private DataOutputStream dataStreamOut;
    private P2PClient client;
    private final int BUFF = 1024;
    
    public RecThread(Socket socket, P2PClient client){
        this.socket = socket;
        this.client = client;
        createStreams();
    }

    public void createStreams(){
        try {
            strOut = socket.getOutputStream();
            strIn = socket.getInputStream();
            dataStreamOut = new DataOutputStream(strOut);
            dataStreamIn = new DataInputStream(strIn);
        } catch (IOException e) {
            System.err.println("Error: "+e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                tokens = new StringTokenizer(dataStreamIn.readUTF());
                switch(tokens.nextToken()){
                    //   This will handle the recieving of a file in background process from other user
                    case "send":
                        recipient = null;
                            try {
                                filePATH = tokens.nextToken(); // PATH grabbed as a token
                                recipient = tokens.nextToken(); // Get the recipient username as a token

                                // Get the folder path where the file will be sent
                                String path = client.getFolderPATH()+filePATH;                                
                                
                                // Create the stream for reading the input
                                BufferedInputStream inputStream = 
                                    new BufferedInputStream(new ProgressMonitorInputStream(client, 
                                        "Getting file from connected peer.", socket.getInputStream()));

                                byte[] buffer = new byte[BUFF];

                                FileOutputStream fileStream = new FileOutputStream(path);
                                int in = 0;
                                while((in = inputStream.read(buffer)) != -1){
                                    fileStream.write(buffer, 0, in);
                                }

                                // File stream cleanup 
                                fileStream.flush();
                                fileStream.close();
                            } catch (IOException e) {
                                DataOutputStream errorDataStream = new DataOutputStream(socket.getOutputStream());

                                // Write error message to the socket
                                errorDataStream.writeUTF("send_resp "+recipient+"Connection to peer lost.");

                                // Finally close the socket 
                                socket.close(); 
                            }
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("End Transfer");
        }
    }
}

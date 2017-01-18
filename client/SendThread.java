import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class SendThread implements Runnable {
    
    protected String file, receiver, sender;
    protected Socket socket;
    private DataOutputStream dataStream;
    protected SendFileToPeer fileSendWindow;
    private final int BUFF = 1024;
    
    public SendThread(Socket socket, String file, String receiver, String sender, SendFileToPeer fileSendWindow){
        this.socket = socket;
        this.file = file;
        this.receiver = receiver;
        this.sender = sender;
        this.fileSendWindow = fileSendWindow;
    }

    // Get the PATH for the file being sent
    public String getFilePath(){
        return this.file;
    }

    // Get who is sending the file
    public String getSender(){
        return this.sender;
    }

    // Get who the file is going to
    public String getReciever(){
        return this.receiver;
    }

    @Override
    public void run() {
        try {
            // turns off the gui at the beggining of the opperation
            fileSendWindow.GUI(true);

            // New output stream for file transfer
            dataStream = new DataOutputStream(socket.getOutputStream());

            // Get and format the filename for ease of use
            File filename = new File(file);
            String formatName = filename.getName().replace(" ", "_");

            // Create an output stream for sending the file
            OutputStream output = socket.getOutputStream();

            // Stream that will read bytes of the file into thee buffer
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
            byte[] streamBuff = new byte[BUFF];// Buffer for storing file bytes

            // Write to the socket sending the information of what file to send and who it is from
            dataStream.writeUTF("send "+ formatName +" "+ receiver +" "+ sender);
                   
            int in = 0;
            // Read and write bytes from the buffer.
            while((in = inputStream.read(streamBuff)) > 0){
                output.write(streamBuff, 0, in);
            }

            fileSendWindow.updateAttachment(false); //  Update Attachment 
            JOptionPane.showMessageDialog(fileSendWindow, "File transfered", "Sucess", JOptionPane.INFORMATION_MESSAGE);

            // Close the streams after the transfer has completed
            output.flush();
            output.close();
        } catch (IOException e) {
            fileSendWindow.updateAttachment(false); //  Update Attachment
            System.err.println("Error: "+ e.getMessage());
        }
    }
}

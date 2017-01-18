import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class SendFileToPeer extends JFrame {

    protected P2PClient main;
    protected Socket socket;
    protected DataInputStream dataInStream;
    protected DataOutputStream dataStream;
    protected StringTokenizer tokens;
    protected String user, host, sendTo, file;
    protected int port;
    
    public SendFileToPeer() {
        makeFrame();
    }
    
    public boolean connectForTransfer(String user, String host, int port, P2PClient main){
        this.host = host;
        this.user = user;
        this.port = port;
        this.main = main;

        try {
            // Create the connection for the file transfer
            socket = new Socket(this.host, this.port);

            // Create the streams for in/outbound data
            dataStream = new DataOutputStream(socket.getOutputStream());
            dataInStream = new DataInputStream(socket.getInputStream());

            // Write to the socket to establish socket connection
            dataStream.writeUTF("socket_con "+ user);
            
            // Fire up the thread to run
            Thread sft = new Thread(new SendFileThread(this));
            sft.start();
            return true;
        } catch (IOException e) {
            System.err.println("Error: "+e.getMessage());
        }
        return false;
    }
    
    class SendFileThread implements Runnable{

        private SendFileToPeer sendFileThread;

        public SendFileThread(SendFileToPeer sendFileThread){
            this.sendFileThread = sendFileThread;
        }
        
        @Override
        public void run() {
            try {
                while(!Thread.currentThread().isInterrupted()){
                    tokens = new StringTokenizer(dataInStream.readUTF());
                    switch(tokens.nextToken()){
                        case "receive_err": 
                            // Send a message that there was an error when trying to process the request
                            StringBuilder mess = new StringBuilder();
                            while(tokens.hasMoreTokens()){
                                mess.append(" "+tokens.nextToken());
                            }
                            sendFileThread.updateAttachment(false);
                            JOptionPane.showMessageDialog(SendFileToPeer.this, mess.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                            try{
                                socket.close();
                            } catch (IOException e) {
                                System.out.println("Error: "+e.getMessage());
                            }
                            dispose();
                            break;
                        case "receive_acc":  
                            // Create and start the new thread for the file transfer 
                            Thread sft = new Thread(new SendThread(socket, file, sendTo, user, SendFileToPeer.this));
                            sft.start();
                            break;
                        case "send_err":
                            // Create the error message 
                            StringBuilder errorMess = new StringBuilder();
                            while(tokens.hasMoreTokens()){
                                errorMess.append(" "+tokens.nextToken()); // Add each and every token
                            }                                                     

                            // Display that there was an error with sending the message
                            JOptionPane.showMessageDialog(SendFileToPeer.this, errorMess.toString(),"Error", JOptionPane.ERROR_MESSAGE);

                            // Make updates to the GUI and other items
                            sendFileThread.updateAttachment(false);
                            sendFileThread.GUI(false);
                            sendFileThread.sendFileButton.setText("Send File");
                            break;
                        case "send_resp":
                            StringBuilder rmess = new StringBuilder();
                            while(tokens.hasMoreTokens()){
                                rmess.append(" "+tokens.nextToken());
                            }
                            sendFileThread.updateAttachment(false);
                            JOptionPane.showMessageDialog(SendFileToPeer.this, rmess.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                            dispose();
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:makeFrame
    private void makeFrame() {
        filePicker = new JFileChooser();
        txtSendTo = new JTextField();
        sendFileButton = new JButton();
        selectFileLabel = new JLabel();
        txtFile = new JTextField();
        btnBrowse = new JButton();
        sendToLabel = new JLabel();

        // Window properties
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Send a File");
        setAlwaysOnTop(true);
        setResizable(false);
        setType(Window.Type.NORMAL);

        // Set label text 
        selectFileLabel.setText("Select a File:");
        sendToLabel.setText("Send to:");

        // Set button text
        btnBrowse.setText("Browse");
        sendFileButton.setText("Send File");

        // Set properties
        txtFile.setEditable(false);

        // Window adapt.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                appWindowExit(event);
            }
        });

        // Action Listeners
        sendFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                sendFileButtonActionPerformed(event);
            }
        });

        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                btnBrowseActionPerformed(event);
            }
        });

        // Configures the layout DO NOT TOUCH
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(selectFileLabel, GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFile)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowse, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                    .addComponent(sendToLabel, GroupLayout.Alignment.LEADING)
                    .addComponent(txtSendTo, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendFileButton)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(selectFileLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sendToLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSendTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(sendFileButton, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:makeFrame

    private void sendFileButtonActionPerformed(ActionEvent event) {//GEN-FIRST:event_btnSendFileActionPerformed
        // TODO add your handling code here:
        file = txtFile.getText();
        sendTo = txtSendTo.getText();

        if((txtSendTo.getText().length() > 0) && (txtFile.getText().length() > 0)){
            try {
                txtFile.setText("");
                String format = "send_metaInf "+user+" "+sendTo+" "+getFileName(file);
                dataStream.writeUTF(format);
                System.out.println(format);
                sendFileButton.setEnabled(false);
            } catch (IOException e) {
                System.err.println("Error: "+e.getMessage());
            }
        }else{
            JOptionPane.showMessageDialog(this, "Missing Fields","Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnSendFileActionPerformed

    private void appWindowExit(WindowEvent event) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        main.updateAttachment(false);
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error: "+e.getMessage());
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnBrowseActionPerformed(ActionEvent event) {//GEN-FIRST:event_btnBrowseActionPerformed
        // TODO add your handling code here:
        if(filePicker.showOpenDialog(this) == filePicker.APPROVE_OPTION)
            txtFile.setText(filePicker.getSelectedFile().toString());
        else
            txtFile.setText("");
        
    }//GEN-LAST:event_btnBrowseActionPerformed
    
    // Enable or disable the GUI
    public void GUI(boolean bool){
        if(bool){ 
            txtSendTo.setEditable(false);
            btnBrowse.setEnabled(false);
            sendFileButton.setEnabled(false);
            txtFile.setEditable(false);
        } else { 
            txtSendTo.setEditable(true);
            sendFileButton.setEnabled(true);
            btnBrowse.setEnabled(true);
            txtFile.setEditable(true);
        }
    }
    
    public String getFileName(String pathToFile){
        String fileName = new File(pathToFile).getName();
        return fileName.replace(" ", "_");
    }
    
    
    public void updateAttachment(boolean bool){
        main.updateAttachment(bool);
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                SendFileToPeer sf = new SendFileToPeer();
                sf.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Buttons
    private JButton btnBrowse;
    private JButton sendFileButton;

    // File Selector
    private JFileChooser filePicker;

    // Labels
    private JLabel selectFileLabel;
    private JLabel sendToLabel;

    // Fields
    private JTextField txtFile;
    private JTextField txtSendTo;
    // End of variables declaration//GEN-END:variables
}

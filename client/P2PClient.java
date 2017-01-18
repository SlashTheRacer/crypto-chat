import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
/**
 * @author Jacob Reed
 * @author Justin Jarrard
 */
public class P2PClient extends JFrame {
    
    // Some vars need to be accessed outside of this class
    protected String user;
    protected String hostAddr;
    protected int hostPort;
    protected Socket socket;
    protected DataOutputStream dataStream;
    protected boolean open = false;
    private boolean isConnected = false;
    private String folderPATH = "/";
    
    public P2PClient() {
        makeFrame();
    }
    
    public void initFrame(String user, String hostAddr, int hostPort){
        this.user = user;
        this.hostAddr = hostAddr;
        this.hostPort = hostPort;
        setTitle(user);
        connect();
    }
    
    public void connect(){
        try {
            socket = new Socket(hostAddr, hostPort);
            dataStream = new DataOutputStream(socket.getOutputStream());
            dataStream.writeUTF("join_chat "+user);
            
            Thread ct = new Thread(new P2PThread(socket, this));
            ct.start();
            sendMesButton.setEnabled(true);
            isConnected = true; 
        }
        catch(IOException e) {
            isConnected = false;
            JOptionPane.showMessageDialog(this, "Connection Failed","Error: ",JOptionPane.ERROR_MESSAGE);
            getMess(e.getMessage(), "Error: ");
        }
    }
    
    // Check to see if this client is connected
    public boolean isConnected(){
        return this.isConnected;
    }
    
    // Get path 
    public void useDirPath(){
        fileSelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(fileSelect.showDialog(this, "Select Directory") == fileSelect.APPROVE_OPTION){
            folderPATH = fileSelect.getSelectedFile().toString()+"/";
        } else {
            folderPATH = "/";
        }
    }

    // Get the full message with the body and the header
    public void getMess(String msg, String header){
        messageDispPane.setEditable(true);
        getHead(header);
        getMsgContent(msg);
    }
    
    // Get the header of the message
    public void getHead(String header){
        int len = messageDispPane.getDocument().getLength();
        messageDispPane.setCaretPosition(len);
        messageDispPane.replaceSelection(header+":");
    }
   
   // Get the body of the message
    public void getMsgContent(String msg){
        int len = messageDispPane.getDocument().getLength();
        messageDispPane.setCaretPosition(len);
        messageDispPane.replaceSelection(msg +"\n");
    }
    
    // Get the path
    public String getFolderPATH(){
        return this.folderPATH;
    }
    
    // Get the address of the host
    public String getHostAddr(){
        return this.hostAddr;
    }
    
    // Get the port of the host server
    public int getHostPort(){
        return this.hostPort;
    }
    
    // Get the username for this client
    public String getUser(){
        return this.user;
    }
    
    // Check to see if the file is open 
    public void updateAttachment(boolean bool){
        this.open = bool;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:makeFrame
    private void makeFrame() {

        fileSelect = new JFileChooser();
        messageToSend = new JTextField();
        sendMesButton = new JButton();
        messageDispScrollPane = new JScrollPane();
        messageDispPane = new JTextPane();
        appMenu = new JMenuBar();
        fileShareMenu = new JMenu();
        logOutMenu = new JMenu();
        saveChatLog = new JMenuItem();
        sendFileMenuItem = new JMenuItem();
        endSession = new JMenuItem();
        getActiveClients = new JMenuItem();
        clearChat = new JMenuItem();

        // Set text for buttons, labels, etc.
        sendMesButton.setText("Send");
        fileShareMenu.setText("File");
        logOutMenu.setText("Session");
        sendFileMenuItem.setText("Send File");
        saveChatLog.setText("Save Chat To File");
        endSession.setText("Disconnect");
        getActiveClients.setText("Online Users");
        clearChat.setText("Clear Chat History");

        // Add items to the app
        fileShareMenu.add(sendFileMenuItem);
        fileShareMenu.add(saveChatLog);
        fileShareMenu.add(getActiveClients);
        fileShareMenu.add(clearChat);
        logOutMenu.add(endSession);
        appMenu.add(fileShareMenu);
        appMenu.add(logOutMenu);

        // Button States
        sendMesButton.setEnabled(false);

        // Diable editing the main pane
        messageDispPane.setEditable(false);

        // Set pane attr.
        messageDispPane.setBackground(new Color(21, 48, 66));
        messageDispPane.setForeground(new Color(108, 194, 252));

        // App. View Options
        messageDispScrollPane.setViewportView(messageDispPane);
        setJMenuBar(appMenu);

        // Window attr.
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // All action listeners for this app.
        addWindowListener(new WindowAdapter() { // Check if window was closed 
            public void windowClosing(WindowEvent event) {
                appExitActionPerformed(event);
            }
        });

        messageToSend.addActionListener(new ActionListener() { // Check to see if there was an action performed on the box
            public void actionPerformed(ActionEvent event) {
                messageToSendActionPerformed(event);
            }
        });

        sendMesButton.addActionListener(new ActionListener() { // Clicked message button
            public void actionPerformed(ActionEvent event) {
                sendMesButtonActionPerformed(event);
            }
        });
        
        sendFileMenuItem.addActionListener(new ActionListener() { // Clicked menu item
            public void actionPerformed(ActionEvent event) {
                sendFileMenuActionPerformed(event);
            }
        });

        saveChatLog.addActionListener(new ActionListener() { // Clicked menu item
            public void actionPerformed(ActionEvent event) {
                saveChatLogActionPerformed(event);
            }
        });

        endSession.addActionListener(new ActionListener() { // Clicked menu item
            public void actionPerformed(ActionEvent event) {
                endSessionActionPerformed(event);
            }
        });

        getActiveClients.addActionListener(new ActionListener() { // Clicked menu item
            public void actionPerformed(ActionEvent event) {
                getActiveClientsActionPerformed(event);
            }
        });

        clearChat.addActionListener(new ActionListener() { // Clicked menu item
            public void actionPerformed(ActionEvent event) {
                clearChatClientsActionPerformed(event);
            }
        });
        
        // Controls Layout DO NOT TOUCH
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(messageToSend, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendMesButton))
                    .addComponent(messageDispScrollPane, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(messageDispScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(messageToSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(sendMesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:makeFrame

    private void sendMesButtonActionPerformed(ActionEvent event) {//GEN-FIRST:event_sendMesButtonActionPerformed
        // TODO add your handling code here:
        try {
            if(messageToSend.getText().length() != 0){
                StringBuilder content = new StringBuilder();
                content.append(user); // Add the user of the sender
                content.append(" "+messageToSend.getText()); // Add the message to be sent
                
                // Write to the socket to send the message to general chat
                dataStream.writeUTF("send_mes "+content.toString());

                // Write to the output
                getMess(" "+messageToSend.getText(), user);

                // Empty the message
                messageToSend.setText(""); 
            }
        } catch (IOException e) { // When not properly connected/cannot write to socket
            getMess("Message not sent.", "Error: "); 
        }
    }

    private void sendFileMenuActionPerformed(ActionEvent event) {
        // TODO add your handling code here:
        if(!open){
            SendFileToPeer fileSendFrame = new SendFileToPeer();
            // Start file transfer process
            if(fileSendFrame.connectForTransfer(user, hostAddr, hostPort, this)){
                fileSendFrame.setVisible(true);
                open = true;
            } else {
                JOptionPane.showMessageDialog(this, "Cannot connect.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearChatClientsActionPerformed(ActionEvent event){
        if(JOptionPane.showConfirmDialog(this, "Delete Chat History?", "Delete Chat History", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            messageDispPane.setText("");
            JOptionPane.showMessageDialog(this, "Deleted Chat History", "Cleared", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void getActiveClientsActionPerformed(ActionEvent event){
        try{
            String requestinClient = this.getUser();
            dataStream = new DataOutputStream(socket.getOutputStream());
            // Send a message to the server asking for all active clients
            dataStream.writeUTF("client_online "+requestinClient);
        } catch(IOException e) {
            System.err.println("Error: Unable to get active clients.\n"+e.getMessage());
        }
    }

    private void saveChatLogActionPerformed(ActionEvent event){
        // TODO add your handling code here
        JFileChooser chooserChatLog = new JFileChooser();
        chooserChatLog.setDialogTitle("Save Chat");
        // Open JOptionPane to get where the client wants to save the chat to
        if(chooserChatLog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
            File log = chooserChatLog.getSelectedFile();
            try {
                // Begin writing the chat history
                BufferedWriter writer = new BufferedWriter(new FileWriter(log));
                System.out.println("Saving chat to: "+log.getAbsolutePath());
                writer.write(messageDispPane.getText());
                writer.close();
                JOptionPane.showMessageDialog(this, "Saved chat history to: "+log.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                System.err.println("Error: Could not find file."+e.getMessage());
            }
        }
    }

    private void endSessionActionPerformed(ActionEvent event){
        // TODO add your handling code here:
        try{
            // Confirm that the client wishes to end the current session
            if(JOptionPane.showConfirmDialog(this, "Continue Disconnecting?", "Disconnect", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                    String disconnectingClient = this.getUser();
                    dataStream = new DataOutputStream(socket.getOutputStream());
                    dataStream.writeUTF("client_discon "+disconnectingClient);
                    socket.close();
                    System.exit(0);
                }
        } catch(IOException e) {
            System.err.println("Error: Unable to process disconnect");
        }
    }

    private void appExitActionPerformed(WindowEvent event) {
        // TODO add your handling code here:
        if(JOptionPane.showConfirmDialog(this, "Do you want to close?") == 0){
            try {
                String disconnectingClient = this.getUser();
                dataStream = new DataOutputStream(socket.getOutputStream());
                dataStream.writeUTF("client_discon "+disconnectingClient);
                socket.close();
            } catch (IOException e) {
                System.out.println("Error: "+e.getMessage());
            }
            this.dispose();
            System.exit(0);
        }
    }

    private void messageToSendActionPerformed(ActionEvent event) {
        // TODO add your handling code here:
        try {
            dataStream.writeUTF("send_mes "+user+" "+ event.getActionCommand());
            getMess(" "+event.getActionCommand(), user);
            messageToSend.setText("");
        } catch (IOException e) {
            getMess("Cannot send message.", "Error: ");
        }
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                P2PClient client = new P2PClient();
                client.initFrame(args[0], args[1], Integer.parseInt(args[2]));
                if(client.isConnected()){
                    client.setLocationRelativeTo(null);
                    client.setVisible(true);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    //Creates the menu bar for the app
    private JMenuBar appMenu;
    private JMenu fileShareMenu, logOutMenu;
    private JMenuItem sendFileMenuItem, saveChatLog, endSession, getActiveClients, clearChat;

    // File picker for file transfer
    private JFileChooser fileSelect;

    // Makes the message history display
    private JTextPane messageDispPane;
    private JScrollPane messageDispScrollPane;

    // Makes the message to be sent portion of the window
    private JButton sendMesButton;
    private JTextField messageToSend;
    // End of variables declaration//GEN-END:variables
}

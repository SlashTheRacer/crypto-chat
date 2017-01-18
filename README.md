# crypto-chat
Documentation by JACOB REED as of 1/17/17
# Server
#### Running The Server
+ Start by compiling the server 
```bash
cd server
javac *.java
```
+ Next, Start the server on the port you wish to has clients connect to.
```bash
java P2PServer 1111
```
Port = 1111<br><br>
<b>**THIS PORT WILL BE THE ONE USED WHEN STARTING THE CLIENT AND WILL BE HOW THE SERVER CONNECTS TO THE CLIENTS</b>

<hr>
# Client
### Running The Client
+ Start by compiling the client 
```bash
cd client
javac *.java
```
+ After, get the host IP if not connecting to Digital Ocean server
```bash
ifconfig
```
+ Finally start the connection to the server either using the hosst machine IP or Digital Ocean server
```bash
java P2PClient jake 127.0.0.1 1111
```
Username = jake <br>
IP = 127.0.0.1 <br>
Port = 1111<br> <br>
<b>**THE USERNAME, IP, AND PORT WILL VARY.  EACH CLIENT NEEDS A DIFFERERNT USERNAME BUT WILL CONNECT TO THE SAME IP AND PORT</b> <br>
<b>*It is recomended that you connect to the Digital Ocean Server at IP: 67.205.175.195 and Port: 1111 <br> ***Let me know of any connection issues as I will have to log in to my account to fix them.**</b>
<hr>
### Using The Client
#### Sending a file 
1. Navigate to <b> File > Send File</b>. 
2. Browse for the file by clicking <b>...</b> and select the file to transfer.
3. Type the username for who you wish to send the file to.
4. They will hasve to accept the transfer and you will get a notification that the file has been transfered.
5. They will recieve notification that the file has been downloaded to the directory they selected.
6. Close out of the send file dialogue box.

#### Save Chat History
1. Navigate to <b>File > Save Chat To File</b>.
2. Selecte the directory to save the chat to along with the filename.
3. Confirmation of where the file was saved will be displayed.

#### See All Online Clients
1. Navigate to <b>File > Online Users</b>.
2. A message dialog will appear with all of the currently connected clients.

#### Clear Chat History 
1. Navigate to <b>File > Clear Chat History</b>.
2. Confirm clearing the history.
3. Messages will be deleted from your client's view.

#### Disconnecting 
1. Navigate to <b>Session > Disconnect</b>.
2. Confirm disconnecting.

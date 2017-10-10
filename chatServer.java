/**
 *
 * @author hendalzahrani
 * 
 * * Done with the help of a series of online tutorials 
 * & youtube videos for dealing with GUI & java.net exceptions
 * This project was done using netbeans IDE
 * Please use command line to run classes,
 * and run the chatServer class first.
 * To initiate the chat,
 * type:  RING localhost 55555 in the clientClass
 * Have a Nice chat!
 * 
 */
import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class chatServer extends JFrame implements ActionListener {
    
    // GLOBAL VARIABLES DECLARATION
   static ServerSocket server;
   static Socket socket; 
   static boolean connected= false;// True only when the server connects with a client
   JPanel panel;
   static JTextField msgToSend;
   static JTextArea chatBox;
   static JButton Send;
   static DataInputStream dis;
   static DataOutputStream dos;
   
   
// GUI ELEMENTS INITIATION
   public chatServer() throws UnknownHostException, IOException {  
      panel = new JPanel();
      msgToSend = new JTextField();
      chatBox = new JTextArea();
      Send = new JButton("Send");
      this.setSize(500, 500);
      this.setVisible(true);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      panel.setLayout(null);
      panel.setBackground(new Color(0x505050, false));
      this.add(panel);
      chatBox.setBounds(20, 20, 450, 360);
      panel.add(chatBox);
      msgToSend.setBounds(20, 400, 340, 30);
      panel.add(msgToSend);
      Send.setBounds(375, 400, 95, 30);
      panel.add(Send);
      this.setTitle("Server");
      Send.addActionListener(this);
      chatBox.setText("Listening for Clients \n \n");
   }

//This method is called whenever the send button is clicked
   @Override
   public void actionPerformed(ActionEvent e) {
   
      if ((e.getSource() == Send) && (msgToSend.getText() != "")) {

            try {
               dos.writeUTF(msgToSend.getText());
            } 
            catch (Exception e1) {
               try {
                  Thread.sleep(3000);
                  System.exit(0);
               } 
               catch (InterruptedException e2) {
                  e2.printStackTrace();
               }
            }
            msgToSend.setText("");
      }
   }
   
   
   public static void appendToChat(String msg){
   
      chatBox.setText(chatBox.getText() + '\n' + msg);
   }
   
   
   // SERVER GETS INITIATED STARTING FROM HERE 
   
   public static void main(String[] args) throws UnknownHostException, IOException {
   //initiate GUI elements
      new chatServer();
      int ack, ind;
      String msg, command="";
      server = new ServerSocket(55555, 1, InetAddress.getLocalHost());
      socket = server.accept();
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());
      try {
          while(!command.equals("FIN") ){//chat keeps going
              
          msg= dis.readUTF();
          ack=0;
          
          //This block is for extracting the command from the message
          ind= msg.indexOf(' ');
          if(ind != -1){
            command = msg.substring(0, ind);
            msg= msg.substring(ind);
      }
      else{
          command= msg;      
      }
          
      // Taking actions for each command
              switch (command) {
                  case "RING":
                      appendToChat("Connected to client");
                      dos.writeUTF("200 Accept");
                      connected= true;
                      break;
                  case "210":
                      appendToChat("Client: 210 "+ msg);
                      break;
                  case "DATA":
                      appendToChat("Client: "+msg);
                       //calculating ack
                      for(int i=0; i<msg.length(); i++)
                          ack+= (int) msg.charAt(i);
                      dos.writeUTF("210 ACK "+ack);
                      break;
                  case "FIN":
                      dos.writeUTF("220 BYE");
                      // break;
                      break;
                  case "220":
                      appendToChat("Client: 220"+msg);
                      break;
                      
                      case "undefined":
                     appendToChat("undefined"+msg);
                     break;
                          
                  default:
                      dos.writeUTF("undefined command.");
                      break;
              } 
 
         }// while  

            Thread.sleep(2000);
            System.exit(0);
            connected= false;
            
      } //end try
      
      catch (Exception e1) {
         appendToChat("Connection is over.\n");
         try {
            Thread.sleep(3000);
            System.exit(0);
         } 
         catch (InterruptedException e) {
         		// TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
}
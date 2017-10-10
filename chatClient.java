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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class chatClient extends JFrame implements ActionListener {
// GLOBAL VARIABLES DECLARATION
   static Socket socket;
   static boolean connected= false;
   static JPanel panel;
   static JTextField msgToSend;
   static JTextArea chatBox;
   static JButton Send;
   static DataOutputStream dos;
   static DataInputStream dis;


// GUI ELEMENTS INITIATION
   public chatClient() throws UnknownHostException, IOException {
      panel = new JPanel();
      msgToSend = new JTextField();
      chatBox = new JTextArea();
      Send = new JButton("Send");
      this.setSize(500, 500);
      this.setVisible(true);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      panel.setLayout(null);
      panel.setBackground(new Color(0xc0c0c0, false));
      this.add(panel);
      chatBox.setBounds(20, 20, 450, 360);
      panel.add(chatBox);
      msgToSend.setBounds(20, 400, 340, 30);
      panel.add(msgToSend);
      Send.setBounds(375, 400, 95, 30);
      panel.add(Send);
      Send.addActionListener(this);
   
      chatBox.setText("Type: RING localhost 55555 to start chatting.");
      this.setTitle("Client");
      
   }


//This method is called whenever the send button is clicked
   @Override
   public void actionPerformed(ActionEvent e) {
   	// TODO Auto-generated method stub
      if ((e.getSource() == Send) && (msgToSend.getText() != "")) {
            try {
               String m=msgToSend.getText();
               
                // setting the timeout for the RING command in millisecounds.
               if(m.substring(0,3).equals("RING"))
                  socket.setSoTimeout(10000);  
               dos.writeUTF(m);
            } 
            catch (SocketTimeoutException e1) {
                // timeout exception.
         addToHistory("Server: 300 BUSY");
      }
            
            catch (Exception e1) {
               addToHistory("Message sending fail:Network Error");
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
   
   // this method is to add a msg to the history
   public static void addToHistory(String msg){
   
      chatBox.setText(chatBox.getText() + '\n' + msg);
   
   }

   public static void main(String[] args) throws UnknownHostException, IOException {
         
      chatClient chatForm = new chatClient();
      socket = new Socket(InetAddress.getLocalHost(), 55555);
       dos = new DataOutputStream(socket.getOutputStream());
       dis = new DataInputStream(socket.getInputStream());
       int ack, ind;
       String msg="", command="";
      
      try {

         while(!command.equals("FIN") && ! msg.equals("220 BYE")){
           
            msg = dis.readUTF();
            ack=0;
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
                 case "200":
                     addToHistory("Server: 200 "+ msg);
                     socket.setSoTimeout(60000);   // set the timeout in millisecounds.
                     connected= true;
                     break;
                 case "210":
                     addToHistory("Server: 210 "+ msg);
                     break;
                 case "DATA":
                     addToHistory(msg);
                     //calculating ack
                     for(int i=0; i<msg.length(); i++)
                         ack+= (int) msg.charAt(i);
                     dos.writeUTF("210 ACK "+ack);
                     break;
                 case "FIN":
                     dos.writeUTF("220 BYE");
                     //break;
                     break;
                 case "220":
                     addToHistory("Server: 220"+msg);
                     break;
                     
                     case "undefined":
                     addToHistory("undefined"+msg);
                     break;
                 default:
                     dos.writeUTF("undefined command.");
                     break;
             } 

         } // end while 

         Thread.sleep(2000);
         System.exit(0);
         connected= false; 
      } // end try
      
      catch (SocketTimeoutException e) {
                // timeout exception.
         addToHistory("Server: 300 BUSY");
      }
      catch (Exception e1) {
         addToHistory("No Connection.");
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

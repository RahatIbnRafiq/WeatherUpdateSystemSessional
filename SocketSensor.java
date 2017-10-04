import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;



class SocketSensor extends JFrame
		 implements ActionListener {

   JLabel text, clicked;
   JButton button;
   JPanel panel;
   JTextField textField1;
   JTextField textField2;
   JTextField textField3;
   Socket socket = null;
   PrintWriter out = null;
   BufferedReader in = null;

   SocketSensor(){ //Begin Constructor
     text = new JLabel("Text to send over socket:");
     textField1 = new JTextField(20);
     textField2 = new JTextField(20);
     textField3 = new JTextField(20);
     button = new JButton("Click Me");
     button.addActionListener(this);

     panel = new JPanel();
     panel.setLayout(new BorderLayout());
     panel.setBackground(Color.white);
     getContentPane().add(panel);
     panel.add("North", text);
     panel.add("Center", textField1);
     panel.add("East", textField2);
     panel.add("West", textField3);
     panel.add("South", button);
   } //End Constructor

  public void actionPerformed(ActionEvent event){
     Object source = event.getSource();

     if(source == button)
     	{
//Send data over socket
	          String text1 = textField1.getText();
	          String text2 = textField2.getText();
	          String text3 = textField3.getText();
	          String line1="Sensor |"+text1+"/"+text2+":"+text3;
	          out.println(line1);
			  textField1.setText(new String(""));
			  textField2.setText(new String(""));
			  textField3.setText(new String(""));
	//Receive text from server
	       	  try
	       	  	{
			  	  String line = in.readLine();
			  	  //if(line.equals(line1))
			  	 // {
			  	  	System.out.println("Server got the update:" + line);
			  	  //}
		          
		       	} catch (IOException e){
			 	   System.out.println("Read failed");
		       	   System.exit(1);
		       }
     }
  }
  
  public void listenSocket(){
//Create socket connection
     try
     {
	       socket = new Socket("127.0.0.1", 4444);
	       out = new PrintWriter(socket.getOutputStream(), true);
	       in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     } catch (UnknownHostException e) 
     	{
	       System.out.println("Unknown host: 127.0.0.1.eng");
	       System.exit(1);
     } catch  (IOException e) 
     	{
	       System.out.println("No I/O");
	       System.exit(1);
     }
  }

   public static void main(String[] args){
        SocketSensor frame = new SocketSensor();
	    frame.setTitle("Sensor program:enter Data:City,Temperature,Humidity");
        WindowListener l = new WindowAdapter() 
        {
	        public void windowClosing(WindowEvent e) 
	        {
	          System.exit(0);
	        }
        };

        frame.addWindowListener(l);
        frame.pack();
        frame.setVisible(true);
		frame.listenSocket();
  }
}

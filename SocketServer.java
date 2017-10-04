import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import java.io.*;
import java.net.*;

 class Database {
	private static ArrayList<String> database = new ArrayList<String>();
	private static  Iterator<String> itr;//= database.iterator();
	private static ArrayList<String> userDatabase = new ArrayList<String>();
	public Database()
	{
		itr= database.iterator();
	}
	
	public String isPresent(String cityName )
	{
		String s="no update";
		int c=0;
		synchronized (this)
		{
			
			Iterator<String> i=database.iterator();
			while(i.hasNext()&&c==0)
			{
				s=i.next();
				System.out.println(s);
				if(s.contains(cityName))
				{
					c=1;
				}
			}
		}
		
		return (s);
	}
	public void add(String update) 
		{
			synchronized (this) 
			{
				
				database.add(update);
			}
	}
	
	public void show() 
	{
			synchronized (this) 
			{
				Iterator<String> i=database.iterator();
				while(i.hasNext())
				{
					System.out.println(i.next());
				}
			}
	}
	
	public void remove(String update)
	{
		synchronized (database)
		{
			int cityStart=update.indexOf(":");
			Iterator<String> i=database.iterator();
			String cityName=update.substring(cityStart,update.length());
			while(i.hasNext())
			{
				String h=i.next();
				if(h.contains(cityName))
				{
					database.remove(h);
				}
			}
			
		}
		
	}
	
	public int isRegistered(String user)
	{
		int r=0;
		synchronized (this)
		{
			
			Iterator<String> i=userDatabase.iterator();
			while(i.hasNext())
			{
				if(i.next().contains(user))
				{
					r=1;
				}
			}
		}
		
		return r;
	}
	
	public void addSubscriber(String newUser)
	{
		
			synchronized (this) 
			{
					userDatabase.add(newUser);
				//database.add(update);
			}
	} 
	
	
}


class ClientWorker implements Runnable {
  private Socket client;
  private JTextArea textArea;
  private static Database d=new Database();
  //private ArrayList<String>al=new ArrayList<String>();
  
  ClientWorker(Socket client, JTextArea textArea) {
   this.client = client;
   this.textArea = textArea; 
   	//d=new Database();  
  }

  public void run(){
  	synchronized (this)
  	{
	    String line;
	    BufferedReader in = null;
	    PrintWriter out = null;
	    try{
	      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	      out = new PrintWriter(client.getOutputStream(), true);
	    } catch (IOException e) {
	      System.out.println("in or out failed");
	      System.exit(-1);
	    }
	
	    while(true){
	      try
	      	{
	        line = in.readLine();
	//Send data back to client
	        // out.println(line);
	         textArea.append(line);
	         
	         if(line.contains("Sensor"))
	         {
	         	d.add(line);
	         	d.show();
	         	out.println("ami sensor");
	         }
	         
	         else if(line.contains("Subscriber"))
	         {
	         	
	         	int c1=line.indexOf("|");
	         	int c2=line.indexOf("/");
	         	String userName=line.substring(c1+1,c2);
	         	String cityName=line.substring(c2+1,line.length());
	         	String update;
	         	int reg=d.isRegistered(userName);
	         	//out.println(userName+" "+cityName);
	         	
	         	if(reg==0)
	         	{
	         		d.show();
	         		d.addSubscriber(line);
	         		update=d.isPresent(cityName);
	         		
	         	}
	         	else
	         	{
	         		d.show();
	         		update=d.isPresent(cityName);
	         	}
	         	
	         	out.println(update);
	         	
	         	
	         }
	         	
	         	
	         	         
	      }catch (IOException e) 
	       {
	         System.out.println("Read failed");
	         System.exit(-1);
	       }
       } 
  	}
   
    
  }
}

class SocketServer extends JFrame{

   JLabel label = new JLabel("Text received over socket:");
   JPanel panel;
   JTextArea textArea = new JTextArea();
   ServerSocket server = null;

   SocketServer(){ //Begin Constructor
     panel = new JPanel();
     panel.setLayout(new BorderLayout());
     panel.setBackground(Color.white);
     getContentPane().add(panel);
     panel.add("North", label);
     panel.add("Center", textArea);
   } //End Constructor

  public void listenSocket(){
    try{
      server = new ServerSocket(4444); 
    } catch (IOException e) {
      System.out.println("Could not listen on port 4444");
      System.exit(-1);
    }
    while(true){
      ClientWorker w;
      try{
        w = new ClientWorker(server.accept(), textArea);
        Thread t = new Thread(w);
        t.start();
      } catch (IOException e) {
        System.out.println("Accept failed: 4444");
        System.exit(-1);
      }
    }
  }

  protected void finalize(){
//Objects created in run method are finalized when 
//program terminates and thread exits
     try{
        server.close();
    } catch (IOException e) {
        System.out.println("Could not close socket");
        System.exit(-1);
    }
  }

  public static void main(String[] args){
        SocketServer frame = new SocketServer();
	    frame.setTitle("Server Program");
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

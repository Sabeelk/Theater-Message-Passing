package client;
import java.io.*;
import java.net.*;

public class VisitorClient extends Thread{
    private static final int numVisitors = 17;
    private int visitorID;
    final static int port = 333;

	public VisitorClient(int id){
        this.visitorID = id;
		this.run();
    }
    
    public void start(){
        try {
            //connect to the server via socket
            System.out.println("VisitorClient: connecting to server");
            Socket s = new Socket("localhost", port);
            System.out.println("VisitorClient: connected!");

            //create outputstream and send method name
            PrintWriter init = new PrintWriter(s.getOutputStream());
            init.println("visitor");
            init.flush();

            //next send visitorid for object creation
            init.println(visitorID);
            init.flush();

            //loop to send the method id's to server
            int i =1;
            while(i <= 3){
                System.out.println("VisitorClient: Sending method id: " + i );
                init.println(i);
                init.flush();
                i++;
            }
        }
        catch (IOException e){
            System.out.println("Unable to listen to port.");
            e.printStackTrace();
        }
    }

	public void run(){
        this.start();
	}

    //used for testing, should be run from StartClients.java
    public static void main(String[] args){
        for (int i=1; i<=numVisitors; i++)
            new VisitorClient(i).start();
	}
}
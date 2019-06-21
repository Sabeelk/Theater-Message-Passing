package client;
import java.io.*;
import java.net.*;

public class SpeakerClient extends Thread{
    final static int port = 333;

	public SpeakerClient (){
		this.run();
    }
    
    public void start(){
        try {
            //connect to the server via socket
            System.out.println("SpeakerClient: connecting to server");
            Socket s = new Socket("localhost", port);
            System.out.println("SpeakerClient: connected!");

            //create outputstream and send method name
            PrintWriter init = new PrintWriter(s.getOutputStream());
            init.println("speaker");
            init.flush();

            //loop to send the method id's to server
            int i =1;
            while(i <= 3){
                System.out.println("SpeakerClient: Sending method id: " + i );
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
            new SpeakerClient().start();
	}
}
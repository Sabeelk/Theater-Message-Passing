package server;
import java.io.*;
import java.net.*;

public class MainServer{
    final static int port = 333;
    ServerSocket main;

    //method to start mainServer and accept incoming connections
    public MainServer(){
		try {
            ServerSocket ms = new ServerSocket(port);
            System.out.println("Server is now running on port 333");
			while (true) 
			{   
                //accepts incoming connection then creates a socket that is passed to the Subserver thread
                Socket s = ms.accept();
                System.out.println("Connection established");
                new SubServer(s);
            }
		}
		catch (IOException e)
		{
			System.out.println("Unable to listen to port.");
			e.printStackTrace();
		}
	}
    
	public static void main(String[] args){

        //START THE MAIN SERVER**
		new MainServer();
	}
}

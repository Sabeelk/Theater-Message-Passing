package server;
import java.io.*;
import java.net.*;

public class SubServer extends Thread {
	public static Movie movie = new Movie();
	public static Clock clock = new Clock(movie);
	public static Speaker speaker = new Speaker(movie, clock);
	public Visitor visitor;
	public Socket s;
	public BufferedReader bf;
	public String type;
	
	// constructor
	public SubServer(Socket s){
		this.s = s;

		try {
			//accept incoming identifier message from connected client and store it
			InputStreamReader in = new InputStreamReader(s.getInputStream());
			bf = new BufferedReader(in);
			String idInit = bf.readLine();

			//subserver stores the name of the client only once
			//eliminates redundancy since each client gets their own subserver
			System.out.println("Connected to " + idInit + " client");
			this.type = idInit;

			//if client is VisitorClient the visitor id will sent so visitor object can be created
			if(type.equals("visitor")){
				int id = Integer.valueOf(bf.readLine());
				this.visitor = new Visitor(id, movie, clock, speaker);
			}
		}
		catch (IOException e)
		{
			System.out.println("Unable to listen to port.");
			e.printStackTrace();
		}//catch

		//start the thread to recieve methods
		this.start();
	}
	
	public void run(){
		//process and run functions of all sent method numbers
		try 
		{	
			String line;
			//loop and run all methods sent after initial ID
			while((line = bf.readLine()) != null){
				this.runMethod(Integer.valueOf(line));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void runMethod (int methodNumber)
	{
		//all visitor methods case
		if (type.equals("visitor")){
			switch (methodNumber)
			{
				case 1:
					try { visitor.enter(); } catch (InterruptedException e) {}
					break;
				case 2:
					try { visitor.formGroups(); } catch (InterruptedException e) {}
					break;	
				case 3:
					try { visitor.leaving(); } catch (InterruptedException e) {}
					break;			
			}
		}
		//all speaker methods case
		else if (type.equals("speaker"))
		{
			switch (methodNumber)
			{
				case 1: 
					try { speaker.speakerArrives(); } catch (InterruptedException e) {}
					break;
				case 2:
					try { speaker.groupSpeech(); } catch (InterruptedException e) {}
					break;
				case 3:
					try { speaker.speakerLeaves(); } catch (InterruptedException e) {}
					break;				
			}//else
		}
		//all clock methods case
		else
		{
			switch (methodNumber)
			{
				case 1: 
					try { clock.clockStart(); } catch (InterruptedException e) {}
					break;
				case 2:
					try { clock.clockSignal(); } catch (InterruptedException e) {}
					break;
				case 3:
					try { clock.clockSleep(); } catch (InterruptedException e) {}
					break;						
			}//else
		}

	}//runMethod
}

package client;

public class StartClients{
    private static final int numVisitors = 17;
    public static void main(String[] args){
        new ClockClient().start();

        new SpeakerClient().start();

        for (int i=1; i<=numVisitors; i++)
        new VisitorClient(i).start();
    }
}
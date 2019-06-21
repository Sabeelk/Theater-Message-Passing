package server;

// import java.util.*;

class Clock extends Thread {
    public static long time = System.currentTimeMillis();
    Movie movie;
    
    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+":"+m);
    }

    public Clock(Movie movie) {
        setName("Clock");
        this.movie = movie;
    }

    public void clockStart() throws InterruptedException{
        msg("Clock has started");
        try{ Thread.sleep(200); } catch(InterruptedException e){ }
    }
        
    public void clockSignal() throws InterruptedException{
        //clock signals movie, and releases waiting visitors
        synchronized(movie){
            msg("Movie is starting, all visitors are watching");
            movie.inSession = true;

            //movie plays for 200ms
            msg("...");
            msg("...");
            try{ Thread.sleep(300); } catch(InterruptedException e){ }
            msg("...");
            msg("...");

            //notify speaker in his office
            synchronized(movie.office){
                //movie ends and speaker is signalled to enter monitor
                msg("Movie has ended, Speaker is called");
                movie.speakerPeriod = true;
                movie.office.notify();
            }
        }
    }

    public void clockSleep() throws InterruptedException{
        //block on time until next viewing
        synchronized(movie.time){
            movie.inSession = false;
            movie.time.wait();
        }
    }
   
    public void run(){
        //if there are still visitors left, restart clock and notify all visitors in lobby
        while(Visitor.checkVisitors()){
            synchronized(movie){
                movie.notifyAll();
            }
            try{ this.clockStart(); } catch(InterruptedException e){ }
            try{ this.clockSignal(); } catch(InterruptedException e){ }
            try{ this.clockSleep(); } catch(InterruptedException e){ }
        }
        //release the speaker and let him go home if clock is not run
        synchronized(movie.office){
            movie.office.notify();

            msg("Final show finished, Clock is shutting down");
        }
    }
}
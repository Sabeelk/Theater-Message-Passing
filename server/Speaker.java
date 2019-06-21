package server;

// import java.util.*;

class Speaker extends Thread {
    int id;
    Movie movie;
    Clock clock;
    Boolean inOffice = false;       //tracks if speaker is already in office for further cycles

    public static long time = System.currentTimeMillis();
    
    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+":"+m);
    }

    public Speaker(Movie movie, Clock clock) {
        setName("Speaker-");
        this.movie = movie;
        this.clock = clock;
    }

    public void speakerArrives() throws InterruptedException{
        //if people are entering or movie is playing, or speaker is not already in the office speaker waits
        synchronized(movie.office){
            if(movie.speakerPeriod == false && inOffice != true) {
                msg("Speaker has arrived, goes to his office");
                inOffice = true;
                movie.office.wait() ;
            }
        }
    }

    public void groupSpeech() throws InterruptedException{
        //once released, speaker will enter monitor and give his speech
        synchronized(movie){
            msg("Speaker enters the theater and gives short speech");
            msg("...");
            msg("...");
            Thread.sleep(200);
            msg("...");
            msg("...");
            msg("Speech has ended, Speaker asks the visitors to form groups to give out tickets");
            
            //release the visitors
            synchronized(this){
                synchronized(movie.grouping){
                    movie.grouping.notifyAll();
                }
                this.wait();
            }

            //hand out tickets to groups if they have at least one member
            if(!movie.group1.isEmpty()){ msg("Gives tickets to group1");}
            if(!movie.group2.isEmpty()){ msg("Gives tickets to group2");}
            if(!movie.group3.isEmpty()){ msg("Gives tickets to group3");}

            //speaker releases visitors after handing out tickets
            synchronized(this){
                this.notifyAll();
            }
            
            //clear groups for next viewing
            movie.group1.clear();
            movie.group2.clear();
            movie.group3.clear();

            //set speakerperiod to false so speaker block until his turn
            movie.speakerPeriod = false;
        }
    }

    public void speakerLeaves() throws InterruptedException{
        //Speaker goes back to his office to wait for next movie
        synchronized(movie.office){
            inOffice = true;
            msg("Returns to his office");
            movie.office.wait();
        }
    }
   
    public void run(){
        //as long as there are visitors left keep calling speaker
        while(Visitor.checkVisitors()){
            try{ this.speakerArrives(); } catch(InterruptedException e){ }
            try{ this.groupSpeech(); } catch(InterruptedException e){ }
            try{ this.speakerLeaves(); } catch(InterruptedException e){ }
        }
        msg("Speaker is done, Speaker is heading home");
    }
}
package server;
import java.util.*;

class Visitor extends Thread {
    static int[] visitorId = new int[17];                 //will be used to check if movie should loop

    int id;
    Movie movie;
    Clock clock;
    Speaker speaker;

    public static long time = System.currentTimeMillis();

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ":" + m);
    }

    public Visitor(int id, Movie movie, Clock clock, Speaker speaker) {
        setName("Visitor-" + id);
        this.id = id;
        this.movie = movie;
        this.clock = clock;
        this.speaker = speaker;
    }

    //function to check if all visitors have left, by checking the static array
    static Boolean checkVisitors(){
        Boolean check = false;
        for(int i=0; i<=16; i++){
            if(visitorId[i] == 0) check = true;
        }
        return check;
    }

    //Method for entering Visitor
    public void enter() throws InterruptedException{
        synchronized(movie){                        
            while(movie.inSession || movie.isFull) {                    //recheck the condition with while loop
                msg("Movie in session or full, waiting in lobby");
                movie.wait();
            }
            movie.numVisitors++;
            //if this thread is the last fitting visitor, all later threads will block on movie
            msg("Seated & waiting for movie to start");
            if(movie.numVisitors == movie.theaterCapacity) {
                movie.isFull = true;
                msg("Movie theater now full********************");
            }
        }
        //visitors wait on clock while watching movie and speaker
        synchronized(movie.grouping){
            movie.grouping.wait();
        }
    }

    //Method for forming groups
    public void formGroups() throws InterruptedException{ 
        msg("entering group");
        synchronized(movie.grouping){
            if(movie.group1.size() < 3) { 
                movie.group1.add(this);
                msg("has entered group 1");
                movie.formingGroups++;
            }
            else if(movie.group2.size() < 3) { 
                movie.group2.add(this);
                msg("has entered group 2");
                movie.formingGroups++;
            }
            else if(movie.group3.size() < 3) { 
                movie.group3.add(this);
                msg("has entered group 3");
                movie.formingGroups++;
            }
        }

        //if all the visitors have grouped, wake the speaker
        synchronized(speaker){
            if(movie.formingGroups == movie.numVisitors){
                speaker.notify();
                
                //reset the counter for the next viewing
                movie.formingGroups=0;
            } 
            //wait for speaker to give out tickets
            speaker.wait();
        }

    } 

    public void leaving() throws InterruptedException{
        synchronized(movie){
            movie.leaving++; 

            msg("Is browsing the theater");
            Thread.sleep((long)(Math.random() * 20));
            
            //Visitors leave in order
            msg("Exits the theater | " + movie.leaving);

            //generate probability to watch movie again
            Random random = new Random();
            int randomNumber = random.nextInt(100 - 1) + 1;
            if(randomNumber <= 75){
                msg("Wants to watch movie again, heads to the lobby | " + randomNumber + "%");

                //if last visitor is leaving, reset clock and variables
                if(movie.leaving == movie.numVisitors){
                    movie.numVisitors = 0;
                    movie.leaving = 0;
                    movie.isFull = false;
                    //reset clock
                    synchronized(movie.time){
                        movie.time.notify();
                    }
                }
                //if less than 75, visitor will block on movie (go to the lobby)
                movie.wait();
            }
            //if visitor leaves record it in the viditorId array
            else visitorId[(id-1)] = -1;

            //if last visitor is leaving, reset clock and variables
            if(movie.leaving == movie.numVisitors){
                movie.numVisitors = 0;
                movie.leaving = 0;
                movie.isFull = false;
                //reset clock
                synchronized(movie.time){
                    movie.time.notify();
                }

            }
        }
    }

    public void run() {
        msg("Has arrived ");

        //if visitor thread was not recorded to have left, loop the thread again
        while(visitorId[id-1] != -1){
            try { this.enter(); } catch (InterruptedException e) {}

            try { this.formGroups(); } catch (InterruptedException e) {}

            try { this.leaving(); } catch (InterruptedException e) {}
        }
    }
}
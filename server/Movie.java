package server;

//This Movie Theater class serves as the Monitor, makes it conceptually easier
import java.util.*;

class Movie{
    //local Variables (can be accessed outside of fthe monitor)
    final int theaterCapacity = 8;
    int numVisitors = 0;            //tracks number of visitors in the movie theater
    int formingGroups =0;           //tracks number of people when forming groups
    int leaving = 0;                //tracks number of people when they're leaving
    Boolean isFull = false;         //flagto see if movie theater is full
    Boolean inSession = false;      //flag to see if movie is playing  
    Boolean speakerPeriod = false;  //flag for periods where speaker is not allowed to enter (initial run)


    //object to hold the groups when they form 
    ArrayList<Visitor> group1 = new ArrayList<Visitor>();
    ArrayList<Visitor> group2 = new ArrayList<Visitor>();
    ArrayList<Visitor> group3 = new ArrayList<Visitor>();

    //condition variables (notifications / condition Variables)
    Object office = new Object();       //speaker will block on this object
    Object grouping = new Object();     //visitors will block on this object until grouping
    Object time = new Object();         //clock will block on this object for next viewing
} 
package bgu.spl.mics.application.objects;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private Modelv2 []  models;
    private int publications;
    private int papersRead;


    public Student(String name,String department){
        this.name= name;
        this.department = department;
        this.status = null;
        publications = 0;
        papersRead = 0;
    }

    public boolean isMsc(){
        return status == Degree.MSc;
    }

    public void incrementPapersRead(){papersRead++;}
    public void incrementPublications(){publications++;}

}

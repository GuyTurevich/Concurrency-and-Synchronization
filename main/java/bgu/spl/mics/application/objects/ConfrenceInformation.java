package bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation implements Comparable<ConfrenceInformation> {

    private String name;
    private int date;
    ConcurrentHashMap<Student, ConcurrentLinkedDeque<Model>> modelsToPublish= new ConcurrentHashMap<Student, ConcurrentLinkedDeque<Model>>();

    public int getDate(){
        return date;
    }

    public ConcurrentHashMap<Student, ConcurrentLinkedDeque<Model>> getModelsToPublish(){
        return modelsToPublish;
    }

    public void addModel(Model model){
        Student student = model.getStudent();
        if(!modelsToPublish.containsKey(student))
            modelsToPublish.put(student, new ConcurrentLinkedDeque<Model>());
        modelsToPublish.get(student).add(model);
    }

    @Override
    public int compareTo(ConfrenceInformation that) {
        if(this.date < that.date)
            return -1;
        else if(this.date > that.date)
            return 1;
        return 0;
    }
}

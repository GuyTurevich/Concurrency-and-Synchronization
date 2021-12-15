package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Future> {

    private String name;
    private String type;
    private int size;
    private Model model;

    public void setName(String name){
        this.name=name;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setSize(int size){
        this.size=size;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public int getSize(){
        return size;
    }

    public Model getModel(){
        return model;
    }

    public void setModel(Model model){
        this.model=model;
    }

}

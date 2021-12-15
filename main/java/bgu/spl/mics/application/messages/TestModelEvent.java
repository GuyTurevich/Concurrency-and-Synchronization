package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event<Future> {

    private Model model;
    private boolean msc;

    public TestModelEvent(boolean msc,Model model){
        this.msc = msc;
        this.model= model;
    }

    public TestModelEvent(){

    }
    public Model getModel(){
        return model;
    }

    public void setModel(Model model){
        this.model=model;
    }

}

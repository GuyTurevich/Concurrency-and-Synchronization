package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    enum Status {PreTrained,Training,Trained,Tested}

    enum Result {None,Good,Bad}

    private String name;
    private Data data ;
    private Student student;
    private Status status;
    private Result results;


    public Model(String name, Data data, Student student){
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = Status.PreTrained;
        this.results = Result.None;
    }

    public Model(Student student, String type, int size, String name){
        this.name = name;
        this.student = student;
        this.status = Status.PreTrained;
        this.results = Result.None;
        data = new Data(type,size);
    }

    public Data getData(){
        return data;
    }

    public Status getStatus(){
        return status;
    }

    public String getStatusString(){
        if(status == Status.PreTrained)
            return "PreTrained";
        if(status == Status.Training)
            return "Training";
        if(status == Status.Trained)
            return "Trained";
        else
            return "Tested";
    }

    public void setStatusToTrained(){
        status=Status.Trained;
    }

    public void setStatusToTested(){
        status=Status.Tested;
    }

    public void setStatusToTraining (){
        status=Status.Training;
    }

    public Student getStudent(){
        return student;
    }
    public boolean isResultGood(){
        return results==Result.Good;
    }

    public void setResultsGood(){
        results=Result.Good;
    }
    public void setResultsBad(){
        results=Result.Bad;
    }

    public void getResults(){

    }

    public String getName(){
        return name;
    }
}

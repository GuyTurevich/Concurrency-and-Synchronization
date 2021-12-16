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
    private Modelv2[] jsonmodels;
    private Model[] trainModels;
    private int publications;
    private int papersRead;


    public Student(String name, String department) {
        this.name = name;
        this.department = department;
        this.status = null;
        publications = 0;
        papersRead = 0;
        trainModels = new Model[jsonmodels.length];
    }

    public void incrementPapersRead() {
        papersRead++;
    }

    public void incrementPublications() {
        publications++;
    }

    public Model[] getTrainModels() {
        return trainModels;
    }

    //check if Student status is Msc
    public boolean isMsc() {
        return status == Degree.MSc;
    }
    //initialize trainModels array with info from jsonModel array
    public void defineTrainModels() {
        if (!modelIsEmpty()) {

            trainModels = new Model[jsonmodels.length];
            int i = 0;

            for (Modelv2 model : jsonmodels) {
                trainModels[i] = new Model(this, model.getType(), model.getSize(), model.getName());
            }
        }
    }

    public boolean modelIsEmpty(){
        return jsonmodels==null;
    }

}

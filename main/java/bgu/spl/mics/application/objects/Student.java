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
    private Modelv2[] models;
    private Model[] trainModels;
    private int modelsCounter; //counter for models in trainModels,
    private int publications;
    private int papersRead;


    public Student(String name, String department) {
        this.name = name;
        this.department = department;
        this.status = null;
        publications = 0;
        papersRead = 0;
        modelsCounter=0;
        trainModels = new Model[models.length];
    }

    public String getName(){
        return this.name;
    }

    public String getDepartment(){
        return this.department;
    }

    public String getStatus(){
        if(status == Degree.MSc)
            return "MSc";
        else if (status == Degree.PhD)
            return "PhD";
        else
            return null;
    }

    public int getPublications(){
        return publications;
    }

    public int getPapersRead(){
        return papersRead;
    }

    public void increasePapersRead(int numOfPapersRead) {
        papersRead += numOfPapersRead;
    }

    public void increasePublications(int numOfPublications) {
        publications += numOfPublications;
    }

    public int getModelsCounter(){
        return modelsCounter;
    }

    public Model getNextModel(){
        if(modelsCounter == trainModels.length)
            return null;
        return trainModels[modelsCounter++];
    }

    public Model[] getTrainModels() {
        return trainModels;
    }

    public void incrementModelCounter(){modelsCounter++;}


    //check if Student status is Msc
    public boolean isMsc() {
        return status == Degree.MSc;
    }
    //initialize trainModels array with info from jsonModel array
    public void defineTrainModels() {
        if (!modelIsEmpty()) {

            trainModels = new Model[models.length];
            int i = 0;

            for (Modelv2 model : models) {
                trainModels[i] = new Model(this, model.getType(), model.getSize(), model.getName());
                i++;
            }
        }
    }

    public boolean modelIsEmpty(){
        return models ==null;
    }

}

package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;   //number of samples which the GPU has processed for training
    private int size;        //number of samples in the data

    public Data(String type,int size){
        this.type=getTypeByString(type);
        this.size = size;
        processed = 0;
    }

    public Type getTypeByString(String type){
        if (type.equals("Images")) return Type.Images;
        else if (type.equals("Text")) return Type.Text;
        else return Type.Tabular;
    }

    public Type getType(){
        return type;
    }

    public String getTypeString(){
        if(type == Type.Images)
            return "Images";
        else if(type == Type.Text)
            return "Text";
        else if(type == Type.Tabular)
            return "Tabular";
        else
            return null;

    }

    public int getSize(){
        return size;
    }

    public void addProcessed(int processed){
        this.processed +=processed;
    }

    public int getProcessed(){
        return processed;
    }

}

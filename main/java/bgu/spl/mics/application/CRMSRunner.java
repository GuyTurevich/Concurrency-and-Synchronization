package bgu.spl.mics.application;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;



/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */


public class CRMSRunner {
    public static void main(String[] args) {
        Input input = new Input();
        //read file and place into input
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(args[0]);
            input = gson.fromJson(reader ,Input.class);
            reader.close();
        }

        catch(Exception E){
            E.printStackTrace();
        }
    }
}

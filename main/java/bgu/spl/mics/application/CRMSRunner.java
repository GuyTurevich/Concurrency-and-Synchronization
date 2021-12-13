package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */


public class CRMSRunner {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("example_input.json"));
            Map <?,?> map = new HashMap<>();
            map= gson.fromJson(reader, Map.class);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }

            //
//            ArrayList<Student> studentsList = new ArrayList<Student>();
//            JsonObject obj=new JsonObject(map.entrySet());


//            JsonArray students = (JsonArray) map.get("Students");
            for (Object object : map.entrySet()){

                JsonObject student = (JsonObject) object;
                String name = String.valueOf(student.get("name"));
                System.out.println(name);
                //Student newstudent = new Student((String) student.get("name"), (String) student.get("department") , (String) student.get("status"));
            }

            reader.close();
        }catch(Exception E){
            E.printStackTrace();
        }

    }
}

package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.Reader;


/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
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
            input = gson.fromJson(reader, Input.class);
            reader.close();
        } catch (Exception E) {
            E.printStackTrace();
        }

        Student[] students = input.getStudents();
        for (Student student : students) {
            student.defineTrainModels();
        }

        TimeService timeService = new TimeService(input.getTickTime(), input.getDuration());
        StartServices(input);


    }

    private static void StartServices(Input input) {
        GPU[] gpus = input.getGPUS();
        for (int i = 0; i < gpus.length; i++) {
            Thread gpuThread = new Thread(new GPUService("gpu" + i, gpus[i]));
            gpuThread.start();
        }
        CPU[] cpus = input.getCPUS();
        for (int i = 0; i< cpus.length; i++){
            Thread cpuThread = new Thread((new CPUService("cpu"+i, cpus[i])));
            cpuThread.start();
        }
    }
}

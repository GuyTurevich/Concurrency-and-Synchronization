package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
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

        StartServices(input);


    }

    private static void StartServices(Input input) {

        MessageBusImpl messageBus = MessageBusImpl.getInstance();

        GPU[] gpus = input.getGPUS();
        for (int i = 0; i < gpus.length; i++) {
            Thread gpuThread = new Thread(new GPUService("gpu" + i, gpus[i]));
            gpuThread.start();
        }
        CPU[] cpus = input.getCPUS();
        for (int i = 0; i < cpus.length; i++) {
            Thread cpuThread = new Thread((new CPUService("gpu" + i, cpus[i])));
            cpuThread.start();
        }

        Cluster cluster = new Cluster(gpus,cpus);

        Student[] students = input.getStudents();
        for (int i = 0; i < students.length; i++) {
            Thread studentThread = new Thread((new StudentService("student" + i, students[i])));
            studentThread.start();
        }
        ConfrenceInformation[] conferences = input.getConferences();
        for (int i = 0; i < conferences.length; i++) {
            Thread conferencesThread = new Thread((new ConferenceService("conference" + i, conferences[i])));
            conferencesThread.start();
        }
        Thread ticksThread = new Thread(new TimeService(input.getTickTime(), input.getDuration()));
        ticksThread.start();

    }
}

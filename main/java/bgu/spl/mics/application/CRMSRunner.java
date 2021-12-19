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
    public static void main(String[] args) throws InterruptedException {
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

    private static void StartServices(Input input) throws InterruptedException {

        MessageBusImpl messageBus = MessageBusImpl.getInstance();




        CPU[] cpus = input.getCPUS();
        GPU[] gpus = input.getGPUS();
        Cluster cluster = new Cluster(gpus,cpus);
        for (int i = 0; i < gpus.length; i++) {
            Thread gpuThread = new Thread(new GPUService("gpu" + i, gpus[i]));
            gpuThread.start();
        }

        for (int i = 0; i < cpus.length; i++) {
            Thread cpuThread = new Thread((new CPUService("cpu" + i, cpus[i])));
            cpuThread.start();
        }



        Student[] students = input.getStudents();
        for (int i = 0; i < students.length; i++) {
            Thread studentThread = new Thread((new StudentService("student" + i, students[i])));
            studentThread.start();
        }
        ConfrenceInformation[] conferences = input.getConferences();

        Thread conferencesThread0 = new Thread((new ConferenceService("conference", conferences[0])));
        conferencesThread0.start();


        Thread ticksThread = new Thread(new TimeService(input.getTickTime(), input.getDuration()));
        ticksThread.start();

        conferencesThread0.join();
        for (int i = 1; i < conferences.length; i++) {
            Thread conferencesThread = new Thread((new ConferenceService("conference" + i, conferences[i])));
            conferencesThread.start();
            conferencesThread.join();
        }

    }
}

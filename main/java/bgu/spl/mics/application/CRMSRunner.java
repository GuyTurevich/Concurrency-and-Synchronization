package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.LinkedList;


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

        CPU[] cpus = input.getCPUS();
        GPU[] gpus = input.getGPUS();

        LinkedList<Thread> threads = StartServices(input,cpus,gpus);

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }

        String outputString = createOutputString(input,cpus,gpus);
        File outputFile = new File("./output.txt");
        try {
            FileWriter writer = new FileWriter("output.txt");
            writer.write(outputString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


    private static LinkedList<Thread> StartServices(Input input, CPU[] cpus, GPU[] gpus) throws InterruptedException {

        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        Cluster cluster = new Cluster(gpus, cpus);
        LinkedList<Thread> threads = new LinkedList<Thread>();

        for (int i = 0; i < gpus.length; i++) {
            Thread gpuThread = new Thread(new GPUService("gpu" + i, gpus[i], input.getDuration()));
            threads.add(gpuThread);
            gpuThread.start();
        }

        for (int i = 0; i < cpus.length; i++) {
            Thread cpuThread = new Thread((new CPUService("cpu" + i, cpus[i])));
            threads.add(cpuThread);
            cpuThread.start();
        }

        Student[] students = input.getStudents();
        for (int i = 0; i < students.length; i++) {
            Thread studentThread = new Thread((new StudentService("student" + i, students[i])));
            threads.add(studentThread);
            studentThread.start();
        }

        ConfrenceInformation[] conferences = input.getConferences();
        for (int i = 0; i < conferences.length; i++) {
            Thread conferencesThread = new Thread((new ConferenceService("conference" + i, conferences[i])));
            threads.add(conferencesThread);
            conferencesThread.start();
        }

        Thread ticksThread = new Thread(new TimeService(input.getTickTime(), input.getDuration()));
        ticksThread.start();

        return threads;
    }


    public static String modelsToString(Model[] models) {
        String output = "";
        for (Model model : models) {
            output += "\t\t\t\t{\n";
            output += "\t\t\t\t\t\"name\": \"" + model.getName() + "\",\n";
            output += "\t\t\t\t\t\"data\": {\n";
            output += "\t\t\t\t\t\t\"type\": \"" + model.getTypeString() + "\",\n";
            output += "\t\t\t\t\t\t\"size\": " + model.getSize() + "\n";
            output += "\t\t\t\t\t},\n";
            output += "\t\t\t\t\t\"status\": \"" + model.getStatusString() + "\",\n";
            output += "\t\t\t\t\t\"results\": \"" + model.getResultsString() + "\"\n";
            output += "\t\t\t\t},\n";
        }
        return output;
    }


    public static String createOutputString(Input input ,CPU [] cpus, GPU [] gpus) {
        String output = "{\n\t\"students\": [\n";
        for (Student student : input.getStudents()) {
            output += "\t\t{\n\t\t\t\"name\": \"" + student.getName() + "\",\n";
            output += "\t\t\t\"department\": \"" + student.getDepartment() + "\",\n";
            output += "\t\t\t\"status\": \"" + student.getStatus() + "\",\n";
            output += "\t\t\t\"publications\": " + student.getPublications() + ",\n";
            output += "\t\t\t\"papersRead\": " + student.getPapersRead() + ",\n";
            output += "\t\t\t\"trainedModels\": [\n";
            output += modelsToString(student.getTrainModels());
            output += "\t\t\t]\n";
            output += "\t\t},\n";
        }
        output += "\t\"conferences\": [\n";
        for (ConfrenceInformation conference : input.getConferences()) {
            output += "\t\t{";
            output += "\t\t\t\"name\": \"" + conference.getName() + "\",\n";
            output += "\t\t\t\"date\": \"" + conference.getDate() + "\",\n";
            output += "\t\t\t\"publications\": [\n";
            output += modelsToString(conference.getModelsArray());
            output += "\t\t\t]\n";
            output += "\t\t},\n";
        }
        output += "\t ],\n";
        int cpuTimeUsed = 0, gpuTimeUsed = 0, batchesProcessed = 0;
        for (CPU cpu : cpus) {
            cpuTimeUsed += cpu.getTimeUsed();
        }
        for (GPU gpu : gpus) {
            gpuTimeUsed += gpu.getTimeUsed();
            batchesProcessed += gpu.getBatchesProcessed();
        }
        output += "\"cpuTimeUsed\": " + cpuTimeUsed + ",\n";
        output += "\"gpuTimeUsed\": " + gpuTimeUsed + ",\n";
        output += "\"batchesProcessed\": " + batchesProcessed + ",\n";
        output += "\"Duration\": " + input.getDuration() + ",\n";
        return output;
    }
}

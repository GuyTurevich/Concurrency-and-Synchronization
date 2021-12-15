package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;

import java.util.Map;

public class Input {
    private Student [] Students ;
    private String [] GPUS;
    private int [] CPUS;
    private ConfrenceInformation [] Conferences;
    private int TickTime;
    private int Duration;


    public Student [] getStudents (){
        return Students;
    }


    public GPU [] getGPUS(){
        GPU [] gpus = new GPU[GPUS.length];
        for (int i=0 ; i<GPUS.length; i++){
            gpus[i]=new GPU(GPUS[i]);
        }
        return gpus;
    }

    public CPU [] getCPUS(){
        CPU [] cpus = new CPU[CPUS.length];
        for (int i=0 ; i<CPUS.length; i++){
            cpus[i]=new CPU(CPUS[i]);
        }
        return cpus;
    }

    public ConfrenceInformation [] getConferences(){
        return Conferences;
    }
    public int getTickTime (){
        return TickTime;
    }

    public int getDuration(){
        return Duration;
    }

}

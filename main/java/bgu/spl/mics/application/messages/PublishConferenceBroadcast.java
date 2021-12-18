package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PublishConferenceBroadcast implements Broadcast {

    ConcurrentHashMap<Student, ConcurrentLinkedDeque<Model>> modelsToPublish;

    public PublishConferenceBroadcast(ConcurrentHashMap<Student, ConcurrentLinkedDeque<Model>> modelsToPublish){
        this.modelsToPublish = modelsToPublish;
    }

    public ConcurrentHashMap<Student, ConcurrentLinkedDeque<Model>> getModels(){
        return modelsToPublish;
    }
}

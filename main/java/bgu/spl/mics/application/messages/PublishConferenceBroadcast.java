package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.concurrent.ConcurrentLinkedDeque;

public class PublishConferenceBroadcast implements Broadcast {

    private ConcurrentLinkedDeque<Model> models;

    public PublishConferenceBroadcast(ConcurrentLinkedDeque<Model> models){
        this.models = models;
    }
}

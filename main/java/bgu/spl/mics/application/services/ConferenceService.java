package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation confrenceInformation;
    int ticksPassed;
    private ConcurrentLinkedDeque<Model> modelsToPublish;

    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        this.confrenceInformation = confrenceInformation;
    }

    private Callback<TerminationBroadcast> terminateCallback = (TerminationBroadcast terminationBroadcast) -> {
        terminate();
    };

    private Callback<PublishResultsEvent> publishCallback = (PublishResultsEvent publishResultsEvent) -> {
        modelsToPublish.add(publishResultsEvent.getModel());
    };

    private Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast) -> {
        if (++ticksPassed == confrenceInformation.getDate())
            this.sendBroadcast(new PublishConferenceBroadcast(modelsToPublish));
        MessageBusImpl.getInstance().unregister(this);
        terminate();
    };

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TerminationBroadcast.class, terminateCallback);
        this.subscribeEvent(PublishResultsEvent.class, publishCallback);
        this.subscribeBroadcast(TickBroadcast.class,tickCallback);
    }


}

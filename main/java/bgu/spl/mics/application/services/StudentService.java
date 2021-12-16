package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private MessageBusImpl messageBus = MessageBusImpl.getInstance();



    private Callback<TrainModelEvent> trainModelCallBack = (TrainModelEvent trainModelEvent)-> {
        trainModelEvent.getModel().setStatusToTrained();
        this.sendEvent(new TestModelEvent(trainModelEvent.getModel().getStudent().isMsc(),trainModelEvent.getModel()));
    };

    private Callback<TestModelEvent> TestModelCallBack = (TestModelEvent testmodelEvent) -> {
        testmodelEvent.getModel().setStatusToTested();
        if (testmodelEvent.getModel().isResultGood())
            this.sendEvent(new PublishResultsEvent(testmodelEvent.getModel()));
        //make sure to change the "result" value of model in the gpuservice   -  maybe has something to do with future
    };

    private final Callback<PublishResultsEvent> PublishResultsCallBack = (PublishResultsEvent publishResultsEvent)->{

    };

    public StudentService(String name) {
        super("Student_Service");
        // TODO Implement this
    }

    @Override
    protected void initialize() {
//        messageBus.register(this);
//        if (!student.modelIsEmpty()){
//            messageBus.sendEvent()
//        }

    }
}

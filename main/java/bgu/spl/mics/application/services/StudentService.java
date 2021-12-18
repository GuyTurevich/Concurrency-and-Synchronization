package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private MessageBusImpl messageBus = MessageBusImpl.getInstance();
    private Model currentModel;
    private Future<Model> currentFuture;


    private Callback<TickBroadcast> tickBroadcastCallback = (TickBroadcast tickBroadcast) -> {


        if (currentFuture == null) {
            currentModel = student.getNextModel();
            if(currentModel != null)
                currentFuture = this.sendEvent(new TrainModelEvent(currentModel));
        }
        else if (currentFuture.isDone()) {
            String status = currentModel.getStatusString();

            if (status.equals("Trained")) {
                currentFuture = this.sendEvent(new TestModelEvent(student.isMsc(), currentModel));
            }
            else if (status.equals("Tested")) {
                if (currentModel.isResultGood())
                    this.sendEvent(new PublishResultsEvent(currentModel));
                currentFuture = null;
            }
        }
        };

    private Callback<PublishConferenceBroadcast> publishConferenceCallback =
            (PublishConferenceBroadcast publishConferenceBroadcast) ->{

                ConcurrentHashMap<Student, ConcurrentLinkedDeque<Model>> models= publishConferenceBroadcast.getModels();
                if(models.containsKey(student)) {
                    ConcurrentLinkedDeque<Model> studentsPublications= models.get(student);
                    student.increasePublications(studentsPublications.size());
                    models.remove(student);
                }
                for(Student currentStudent : models.keySet())
                    student.increasePapersRead(models.get(currentStudent).size());


    };

    private Callback<TerminationBroadcast> terminateCallback = (TerminationBroadcast terminationBroadcast) ->{
        terminate();
    };

    public StudentService(String name, Student student) {
        super(name);
        this.student = student;

    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, tickBroadcastCallback);
        this.subscribeBroadcast(PublishConferenceBroadcast.class, publishConferenceCallback);
        this.subscribeBroadcast(TerminationBroadcast.class, terminateCallback);
        }
    }


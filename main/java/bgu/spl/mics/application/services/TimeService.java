package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

    private int tickDuration;
    private int totalDuration;
    private int ticksPassed;
    private TickBroadcast tickBroadcast;

    public TimeService(int tickDuration, int totalDuration) {
        super("Clock");
        this.tickDuration = tickDuration;
        this.totalDuration = totalDuration;
        this.tickBroadcast = new TickBroadcast();
        ticksPassed = 0;
    }

    private Callback<TerminationBroadcast> terminateCallback = (TerminationBroadcast terminationBroadcast) -> {
        terminate();
        System.out.println(this.getName() + " Terminated");
    };

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TerminationBroadcast.class, terminateCallback);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ticksPassed++;
                if (ticksPassed * tickDuration < totalDuration) {
                    sendBroadcast(tickBroadcast);
                    if(ticksPassed %200==0)
                        System.out.println(ticksPassed /200);
                    if(ticksPassed > 5997)
                        System.out.println();

                }
                else if(ticksPassed * tickDuration >= totalDuration) {
                    sendBroadcast(new TerminationBroadcast());
                    terminate();
                }
            }
        }, tickDuration, tickDuration);


    }

}

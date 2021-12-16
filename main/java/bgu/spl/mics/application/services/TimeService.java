package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int tickDuration;
	private int totalDuration;
	private int timePassed;

	public TimeService(int tickDuration, int totalDuration) {
		super("Clock");
		this.tickDuration = tickDuration;
		this.totalDuration = totalDuration;
	}

	private Callback<TickBroadcast> TickBroadcastCallback = (TickBroadcast tickbroadcast) ->{
		if(timePassed < totalDuration)
			timePassed++;
	};

	@Override
	protected void initialize() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate((TimerTask) TickBroadcastCallback, new Date(), tickDuration);
		
	}

}

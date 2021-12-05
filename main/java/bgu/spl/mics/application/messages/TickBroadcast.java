package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Future;


public class TickBroadcast implements Broadcast {
    private Future<Boolean> result;

    public TickBroadcast(){
        result=null;
    }


}

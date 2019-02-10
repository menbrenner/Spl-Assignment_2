package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private static  TimeService instance =null;
	private int currentTick;
	public static TimeService  getInstance(){
		return instance;
}
	/**
	 *The constructor initializes the fields.
	 */
	public TimeService() {
		super("Time");
		currentTick=0;
	}
	/**
	 *subscribes to wanted events and broadcasts
	 */
	@Override
	protected synchronized void initialize() {        
        	while(currentTick!= duration)
        	{   

        		try {
        			Thread.sleep(speed);
        		}catch(Exception e) {
        			e.printStackTrace();
				}
        		currentTick++;
				TickBroadcast b=new TickBroadcast(currentTick,duration);
				sendBroadcast(b);
        	}
    		terminate();
          }
	}

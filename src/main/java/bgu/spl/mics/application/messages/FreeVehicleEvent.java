package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
//from: logistics service
//to: resources service
public class FreeVehicleEvent implements Event<Boolean> {
	private DeliveryVehicle vehicle;

	public FreeVehicleEvent(DeliveryVehicle vehicle)
	{
		this.vehicle=vehicle;
	}

	public DeliveryVehicle getVehicle() {
		return vehicle;
	}


}

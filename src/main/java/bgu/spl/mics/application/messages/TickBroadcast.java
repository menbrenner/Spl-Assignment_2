package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
//from: timeService.
//to:everyone.
public class TickBroadcast implements Broadcast{
	private int CurrentTick;
	private int duration;

	public TickBroadcast(int currentTick , int duration) {
		this.CurrentTick=currentTick;
		this.duration = duration;
	}

	public int getTick() {
		return CurrentTick;
	}

	public int getDuration(){
		return duration;
	}
}

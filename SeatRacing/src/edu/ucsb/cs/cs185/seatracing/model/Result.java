package edu.ucsb.cs.cs185.seatracing.model;

public class Result {
	private int roundID;
	private int rowerID;
	private int boatID;
	private int time;
	private int date;
	
	public Result(int round, int rower, int boat, int timeIn, int dateIn){
		roundID = round;
		rowerID = rower;
		boatID = boat;
		time = timeIn;
		date = dateIn;
	}
	
	public int round(){
		return this.roundID;
	}
	
	public int rower(){
		return this.rowerID;
	}
	
	public int boat(){
		return this.boatID;
	}
	
	public int time(){
		return this.time;
	}
	
	public int date(){
		return this.date;
	}

}
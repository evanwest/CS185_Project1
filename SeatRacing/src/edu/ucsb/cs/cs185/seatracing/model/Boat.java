package edu.ucsb.cs.cs185.seatracing.model;

import edu.ucsb.cs.cs185.seatracing.BoatRowerNameFragment;
import android.os.Bundle;

public class Boat {

	private int size;
	private String name;
	private Rower[] rowers;

	public Boat(int id, Bundle lineup){
		loadFromBundle(id, lineup);
	}

	public Boat(String name, int size){
		this.size=size;
		this.name = name;
	}

	public void setRowers(String... rowers){
		if(rowers.length != this.size){
			throw new IllegalArgumentException("Cannot fill "+this.size+" size boat with "+rowers.length+" rowers!");
		}

		Rower[] rowerObjs = new Rower[this.size];
		for(int i=0; i<this.size; ++i){
			rowerObjs[i] = new Rower(rowers[i]);
		}
		setRowers(rowerObjs);
	}

	public void setRowers(Rower[] rowers){
		this.rowers = rowers;
	}

	public String[] getRowerNames(){
		String[] names = new String[this.size];
		for(int i=0; i<this.size; ++i){
			names[i] = rowers[i].name();
		}
		return names;
	}

	public String name(){
		return this.name;
	}


	public int size(){
		return this.size;
	}

	public void loadFromBundle(int id, Bundle lineupBundle){
		size = lineupBundle.getInt("numRowers");
		rowers = new Rower[size];
		if(id==0){
			name = lineupBundle.getString("boatAName");
		}
		else if(id==1){
			name = lineupBundle.getString("boatBName");
		}
		for(int j=0; j<size; ++j){
			rowers[j] = new Rower(lineupBundle.getString("rower"+id+"-"+j+"Name"));
		}
	}
	
	public void writeToBundle(int id, Bundle bundle){
		bundle.putInt("numRowers",size);
		if(id==0){
			bundle.putString("boatAName",name);
		}
		else if(id==1){
			bundle.putString("boatBName",name);
		}
		for(int j=0; j<size; ++j){
			bundle.putString("rower"+id+"-"+j+"Name", rowers[j].name());
		}
	}

	/**
	 * 
	 * @param b1 One boat to switch from
	 * @param b2 Another boat to switch from
	 * @param position Position in the boats to switch (0-indexed)
	 */
	public static void switchRowers(Boat b1, Boat b2, int position){
		if(b1.size() != b2.size()){
			throw new IllegalArgumentException("Tried to switch in boats of different sizes!");
		}
		else if(position >= b1.size()){
			throw new IndexOutOfBoundsException("Tried to switch rower "+position+" in boats of size "+b1.size());
		}

		Rower temp = b1.rowers[position];
		b1.rowers[position] = b2.rowers[position];
		b2.rowers[position] = temp;
	}

}
package edu.ucsb.cs.cs185.seatracing.model;

import java.util.Arrays;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Boat implements Parcelable {

	private int size;
	private String name;
	private Rower[] rowers;
	private int id;

	public Boat(int id, Bundle lineup){
		loadFromBundle(id, lineup);
		id = (int)System.currentTimeMillis();
	}

	public Boat(String name, int size){
		this.size=size;
		this.name = name;
		id = (int)System.currentTimeMillis();
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
	
	public void setID(int newID){
		this.id = newID;
	}
	
	public int getID(){
		return this.id;
	}

	public String[] getRowerNames(){
		String[] names = new String[this.size];
		for(int i=0; i<this.size; ++i){
			names[i] = rowers[i].name();
		}
		return names;
	}
	
	public Rower getRower(int position){
		return rowers[position];
	}
	
	public Rower[] getRowers(){
		return rowers;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(size);
		dest.writeString(name);
		dest.writeInt(rowers.length);
		for (int i=0;i<rowers.length;i++){
			rowers[i].writeToParcel(dest,flags);
		}
		
	}
	
    public static final Parcelable.Creator<Boat> CREATOR = new Parcelable.Creator<Boat>() {
    	public Boat createFromParcel(Parcel in) {
    		return new Boat(in);
    	}

    	public Boat[] newArray(int size) {
    		return new Boat[size];
    	}
    };

	private Boat(Parcel in) {
		size = in.readInt();
		name = in.readString();
		int num_rowers = in.readInt();
		rowers = new Rower[num_rowers];
		for(int i=0; i<num_rowers;i++){
			rowers[i] = in.readParcelable(Rower.class.getClassLoader());
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Boat: "+this.name+": [");
		for(Rower r : rowers){
			sb.append(r.toString()+", ");
		}
		sb.append(" ]");
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(rowers);
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Boat other = (Boat) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(rowers, other.rowers))
			return false;
		if (size != other.size)
			return false;
		return true;
	}
	
	/*
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		if(! (o instanceof Boat)){
			return false;
		}
		
		Boat b = (Boat)o;
		
		boolean res = true;
		
		res = res & (this.name() == b.name());
		res = res & (this.rowers.length == b.getRowers().length);
		for(int i=0; i<this.rowers.length; ++i){
			res = res & (this.rowers[i].equals(b.getRowers()[i]));
		}
		
		return res;
	}
	*/

}

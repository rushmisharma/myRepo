package com.test.androidrestconsumer.search;



public class SalsaEventModel  implements Comparable{
	
	String event;
	String date;
	String time;
	String cost;
	String address;
	String eType;
	String distance;
	String travelDuration;
	
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String geteType() {
		return eType;
	}
	public void seteType(String eType) {
		this.eType = eType;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getTravelDuration() {
		return travelDuration;
	}
	public void setTravelDuration(String travelDuration) {
		this.travelDuration = travelDuration;
	}
	
	
	@Override
	public int compareTo(Object arg0) {
		String compareage=((SalsaEventModel)arg0).getDistance();
        /* For Ascending order*/
        return this.getDistance().compareTo(compareage);

        /* For Descending order do like this */
        //return compareage-this.studentage;
	}
}

/* Fuel.java */
/* 30/07/12 */

package it.ismb.automotive;

public class Fuel {
	
	private double lat = 0f;
	private double lon = 0f;
	private String info = "";
	
	public Fuel(double lat, double lon, String info) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.info = info;
	}

	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}

}

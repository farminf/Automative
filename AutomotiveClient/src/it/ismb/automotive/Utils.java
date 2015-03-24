
/* Utils.java */
/* 30/07/12 */

package it.ismb.automotive;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Utils {

	public static String getCurrentTime() {
		String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(new Date());
		return currentTimeString;
	}
	
	/**
	 * Extract from a String like lat1&lon1.18&description1&lat2&lon2&description2&...
	 * ordered values, returned in Fuel array.
	 *  
	 * @param source
	 * @return Fuel[]
	 */
	public static Fuel[] extractFuelList(String source) {		
		try {
			StringTokenizer st = new StringTokenizer(source, Constants.SEPARATOR_FUEL);
			String arr[];

			//the array size is the number of tokens in the String:
			arr = new String[st.countTokens()];

			int i=1;
			while (st.hasMoreElements()) {
				arr[i-1] = ""+st.nextElement();
				i++;
			}

			int sizeFuelArray = (arr.length) / 3;
			Fuel[] fuelArray = new Fuel[sizeFuelArray];
			int k = 0;

			for (int j=0; j < arr.length; j=j+3) {
				double lat = Double.parseDouble(arr[j]);
				double lon = Double.parseDouble(arr[j+1]);
				String info = arr[j+2];

				Fuel f = new Fuel(lat, lon, info);
				fuelArray[k] = f;
				k++;
			}

			return fuelArray;
		}
		catch (Exception e) { // never
			return null;
		}
	}
	
	public static double getMinLatValue(Fuel[] f) {
		double min = 0.0;	
		for (int i=0; i < f.length; i++) {
			 if (f[i].getLat() < min) {
				 min = f[i].getLat();
			 }
		}
		return min;
	}

	public static double getMinLonValue(Fuel[] f) {
		double min = 0.0;	
		for (int i=0; i < f.length; i++) {
			 if (f[i].getLon() < min) {
				 min = f[i].getLon();
			 }
		}
		return min;
	}
	
	public static double getMaxLatValue(Fuel[] f) {
		double max = 0.0;	
		for (int i=0; i < f.length; i++) {
			 if (f[i].getLat() > max) {
				 max = f[i].getLat();
			 }
		}
		return max;
	}

	public static double getMaxLonValue(Fuel[] f) {
		double max = 0.0;	
		for (int i=0; i < f.length; i++) {
			 if (f[i].getLon() > max) {
				 max = f[i].getLon();
			 }
		}
		return max;
	}
	
}
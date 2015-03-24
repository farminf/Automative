
/* Constants.java */
/* 11/09/12 */

package it.ismb.automotive;

public class Constants {
	
	// request code for identify launched activity
	public static final int REQUEST_CODE_CONNECTION = 1111;
	public static final int REQUEST_CODE_GPS = 2222;
	public static final int REQUEST_CODE_SETTINGS = 3333;
	public static final int REQUEST_CODE_INSERT_TEXT = 4444;
	public static final int REQUEST_CODE_TOPIC = 5555;
	
	// where write information: notifications, log or both
	public static final int VIEW_NOTIFICATIONS = 0;
	public static final int VIEW_LOG = 1;
	public static final int VIEW_BOTH = 2;
	
	// Message Type
	public static final int TYPE_TRAFFIC = 0;
	public static final int TYPE_WEATHER = 1;
	public static final int TYPE_CRASH = 2;
	public static final int TYPE_PROBLEM_FUEL = 3;
	
	public static final String MSG_PROBLEM_FUEL = "PROBLEM_FUEL";
	public static final String SEPARATOR_FUEL = "&";
	
	// GPS defaul value if error in preferences file
	public final static int GPS_MIN_TIME = 10000;  	// the minimum time interval for notifications, in milliseconds. 
	   													// This field is only used as a hint to conserve power, and actual 
    													// time between location updates may be greater or lesser than this value.
	public final static int GPS_MIN_DISTANCE = 20; // the minimum distance interval for notifications, in meters;
	
	public static final String NO_LOCATION_AVAILABLE = "No location available";
	
	public static final int PRESENCES_INITIAL_DELAY_FOR_START_SENDING = 5000;
	public static final int PRESENCES_FREQUENCY = 10000;
	
	// RESULTS_NUMBER_FROM_SERVER default value if error in preferences file
	public final static int RESULTS_NUMBER_FROM_SERVER = 5; 

	// used to schedule sending message received from REPLY app via Chat to Manager.
	public static final int REPLY_INITIAL_DELAY_FOR_START_SENDING = 5000;
	public static final int REPLY_FREQUENCY = 10000;
	
	// Body of Chat Message sent to server to specify how forward message
	public static final String MSG_TO_ALL = "Msg to all";
	public static final String MSG_ONLY_NEAR = "Msg only near";
	
	// Presence Priority
	public static final int PRESENCE_PRIORITY = 1;
	
	// topic pubsub nodes
	public static final String TOPIC_AIR_POLLUTION = "Air Pollution";
	public static final String TOPIC_ALERT = "Alert";
	public static final String TOPIC_NEWS = "News";
	public static final String TOPIC_PARKING = "Parking Availability";
	public static final String TOPIC_ROAD_QUALITY = "Road Quality";
	public static final String TOPIC_TRAFFIC = "Traffic";
	public static final String TOPIC_WEATHER = "Weather";
	
	// REPLY Message Type
	public static final int REPLY_TYPE_ENGINE = 0;
	public static final int REPLY_TYPE_TIRE = 1;
	public static final int REPLY_TYPE_FUEL = 2;
	// REPLY Intent key
	public static final String REPLY_ITEM_KEY = "PROBLEMA";
	
	public static final String DEFAULT_STRING_VALUE = "";
	
	// Type used to display different icon on onTap() in map markers 
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_FUEL = 1;
	public static final int TYPE_ENGINE = 2;
	
	// fake coordinates for demo
	public static final Double FAKE_LAT = 45.10250;
	public static final Double FAKE_LON = 7.66841;
	
}

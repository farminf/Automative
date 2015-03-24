
/* AutomotiveClient.java */
/* 11/09/12 */

/* Note: */
/* 1) Application can work with GPS disabled (precences will not be sent to server) */
/* 2) Fake coordinates only for Demo if GPS signal not available   					*/

package it.ismb.automotive;

import it.ismb.automotive.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class AutomotiveClient extends MapActivity implements RosterListener {
	
	private XMPPConnection connection = null;
	
	// Need handler for callbacks to the UI thread
	private Handler presencesHandler = null;
    private Handler connectionHandler = null;
    
	// server parameters
	private static String REMOTE_SERVER_ADDRESS = ""; // 130.192.85.33
	private static int REMOTE_SERVER_PORT = -1; // 5222
	private static String REMOTE_SERVER_NAME = ""; // ripley
	private static String REMOTE_PUBSUB_SERVICE = ""; // pubsub.ripley
	private static String REMOTE_RESOURCE = ""; // IOTS
	private static String REMOTE_MANAGER_ACCOUNT = ""; // manager_dispositivi_thomas
	
	// client parameters
	private static String CLIENT_USERNAME = ""; // bolognesi
	private static String CLIENT_PASSWORD = ""; // coccolino69
	private static String CLIENT_DESCRIPTION = ""; // Smart client for testing
	private static String CLIENT_SUBSCRIPTION = ""; // SUBSCRIBE_ALL

	// GPS configuration
	private boolean useGps = true;				   // if "true" I want use GPS in application
	private boolean displayCoordinates = true;	   // if "true" I want display coordinates into device screen

	private int gpsMinTime = Constants.GPS_MIN_TIME; 
	private int gpsMinDistance = Constants.GPS_MIN_DISTANCE;
	private LocationManager mlocManager = null; // LocationManager class to obtain GPS locations.
	private LocationListener mlocListener = null; // location listener
	
	// Message forwarding configuration
	private boolean sendToAll = true;	// If false, server must be forward messages only to vehicles near sender
	
	// verbosity configuration
	private boolean useVerbosity = true; // if "true" I want use more verbosity in application (eg timestamp)
	private boolean useAlert = true; // if "true" I want use Alert for display receveing messages
	private boolean displayMarkers = true;	   // if "true" I want display markers on map
	private int resultsNumber = Constants.RESULTS_NUMBER_FROM_SERVER;
	private boolean useFakeCoordinates = true;
		
	// Sending presences with GPS info into status
	private boolean sendPresences = true; // if "true" I want send presences, with embedded lat and lon
	private int presencesFrequency = Constants.PRESENCES_FREQUENCY; 
	private int presencesDelay = Constants.PRESENCES_INITIAL_DELAY_FOR_START_SENDING;
	private Double latitudeToSent = -1.0;
	private Double longitudeToSent = -1.0;
	
	// Topic Configuration
	private boolean subscribeAir = true;
	private boolean subscribeAlert = true;
	private boolean subscribeNews = true;
	private boolean subscribeParking = true;
	private boolean subscribeRoad = true;
	private boolean subscribeTraffic = true;
	private boolean subscribeWeather = true;
	
	// Listener for topic
	private MyItemEventListener listenerAir = new MyItemEventListener(Constants.TOPIC_AIR_POLLUTION);
	private MyItemEventListener listenerAlert = new MyItemEventListener(Constants.TOPIC_ALERT);
	private MyItemEventListener listenerNews = new MyItemEventListener(Constants.TOPIC_NEWS);
	private MyItemEventListener listenerParking = new MyItemEventListener(Constants.TOPIC_PARKING);
	private MyItemEventListener listenerRoad = new MyItemEventListener(Constants.TOPIC_ROAD_QUALITY);
	private MyItemEventListener listenerTraffic = new MyItemEventListener(Constants.TOPIC_TRAFFIC);
	private MyItemEventListener listenerWeather = new MyItemEventListener(Constants.TOPIC_WEATHER);
	
	private PubSubManager psm = null;

	private Button buttonTraffic = null;
	private Button buttonWeather = null;
	private Button buttonCrash = null;
	private Button buttonMessage = null;
		
	// Default messages
	private String MSG_TRAFFIC = "";
	private String MSG_WEATHER = "";
	private String MSG_CRASH = "";
	
	private ProgressDialog dialog = null;
	private boolean operationIsInterruped = false; // user cancel connecting operation
	
	private boolean packetFilterAlreadyCreated = false;
	
	// BroadcastReceiver for notify network "not connected"events
	private ConnectivityReceiver receiver = null;
	private boolean networkAvailable = true;
		
	private ConnectionConfiguration config;
	
	private static final String TAG = "AutomotiveClient";
	
	// Map View
	private MapView mapView;
	
	// A utility class to manage panning and zooming of a map.
	private MapController mapController;
	
	// An Overlay for drawing the user's current location (and accuracy) on the map, and/or a compass-rose inset.
	// Subclases can override dispatchTap() to handle taps on the current location. 
	private MyLocationOverlay myLocationOverlay;
	
	// An immutable class representing a pair of latitude and longitude, stored as integer numbers of microdegrees.
	private GeoPoint point;
	private MyOverlays myOverlays;
	
	private TextView textNotifications = null;
	private ScrollView scrollerNotifications = null;
	private Button bClearNotifications = null;
	private ImageView imageViewStatusServer= null;
	
	private TextView textLog = null;
	private ScrollView scrollerLog = null;
	private Button bClearLog = null;
	
	// Timer used to schedule sending message received from REPLY app via Chat to Manager.
	private Timer myReplyTimer = null;
	
	 // TabHost (MESSAGES + MAP + LOG)
    TabHost tabs = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the activity content from a layout resource. The resource will be inflated, adding 
        // all top-level views to the activity.
        setContentView(R.layout.main);
        
    	// init TextView and ScrollView
		textNotifications = (TextView) findViewById(R.id.textNotifies);
		scrollerNotifications = (ScrollView) findViewById(R.id.scroller_notifications);
		
		// init TextView, ScrollView and ImageView
		textLog = (TextView) findViewById(R.id.textLog);
		scrollerLog = (ScrollView) findViewById(R.id.scroller_log);
		imageViewStatusServer = (ImageView)findViewById(R.id.serverStatus);
		
    	MSG_TRAFFIC = getText(R.string.default_message_traffic).toString();
    	MSG_WEATHER = getText(R.string.default_message_weather).toString();
    	MSG_CRASH = getText(R.string.default_message_accident).toString();
        
    	Intent localIntent = getIntent();
    	if (localIntent != null) {
    		int code = localIntent.getIntExtra(Constants.REPLY_ITEM_KEY, -1);
    		analyzeCodeReply(code);
    		// set intent to avoid return in this case (es. when rotating)
    		setIntent(new Intent());
    	}
        
        // Sets the default values from a preference hierarchy in XML. This should be called by the application's main 
        // activity.
    	PreferenceManager.setDefaultValues(this, R.xml.preferences_topic, true);
    	PreferenceManager.setDefaultValues(this, R.xml.preferences_connection, true);
        PreferenceManager.setDefaultValues(this, R.xml.preferences_gps, true);
        PreferenceManager.setDefaultValues(this, R.xml.preferences_settings, true);
        
        // TabHost (MESSAGES + MAP + LOG)
        tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec=tabs.newTabSpec(getString(R.string.tab_messages));
        spec.setContent(R.id.tabMessages);
        spec.setIndicator(getString(R.string.tab_messages),getResources().getDrawable(R.drawable.tab_messages));
		tabs.addTab(spec);
		        
		spec=tabs.newTabSpec(getString(R.string.tab_map));
		spec.setContent(R.id.tabMap);
		spec.setIndicator(getString(R.string.tab_map),getResources().getDrawable(R.drawable.tab_map));
		tabs.addTab(spec);

		spec=tabs.newTabSpec(getString(R.string.tab_log));
		spec.setContent(R.id.tabLog);
		spec.setIndicator(getString(R.string.tab_log),getResources().getDrawable(R.drawable.tab_log));
		tabs.addTab(spec);
		
		// here I must init variables from preferences file
		configureGpsAnalyzingPreferencesFile();
		configureSettingsAnalyzingPreferencesFile();
		configureTopicAnalyzingPreferencesFile();
        
        presencesHandler = new Handler();
        connectionHandler = new Handler();
        
	    // Map View
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		mapView.setTraffic(false);
		
		// Controller map
		mapController = mapView.getController();
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		myLocationOverlay.disableCompass(); // refresh problem?
		mapView.getOverlays().add(myLocationOverlay);
		mapView.postInvalidate();
		
		// Set icon for overlays
		Drawable marker = getResources().getDrawable(R.drawable.marker_normal);
		myOverlays = new MyOverlays(marker, this); 
		
		CheckBox cbSatellite = (CheckBox) findViewById(R.idCheckBox.satellite);
		cbSatellite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mapView.setSatellite(isChecked);
			}
		});
		
		CheckBox cbTraffic = (CheckBox) findViewById(R.idCheckBox.traffic);
		cbTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mapView.setTraffic(isChecked);
			}
		});
		
		// Button for remove overlays
		Button bClear = (Button) findViewById(R.idButton.clear_overlays);
		bClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myOverlays.clear();
				mapView.getOverlays().clear();
				mapView.getOverlays().add(myOverlays);
				mapView.invalidate();
			}
		});
		
		// Button for center the map on the last overlay
		Button bCenter = (Button) findViewById(R.idButton.map_center);
		bCenter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myOverlays.size() != 0) {
					mapController.animateTo(myOverlays.getItem(myOverlays.size()-1).getPoint());
				}
			}
		});
		
		// Button for clear notification
		bClearNotifications = (Button) findViewById(R.idButton.clear_notifications);
		bClearNotifications.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textNotifications.setText("");				
			}
		});
		
		// Button for clear log
		bClearLog = (Button) findViewById(R.idButton.clear_log);
		bClearLog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textLog.setText("");				
			}
		});

		initialCommonsOperations();
		
        receiver = new ConnectivityReceiver();
        // System will broadcast this whenever a change in the network connectivity occurs.
        // Register a BroadcastReceiver to be run in the main activity thread. The receiver will be called with any 
        // broadcast Intent that matches filter, in the main application thread. 
        registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    
	private void analyzeCodeReply(int code) { // FIXME vedere come gestire queste notifiche
		if ((code == Constants.REPLY_TYPE_ENGINE) || (code == Constants.REPLY_TYPE_TIRE)) {
		 	Toast.makeText(this, "Problema meccanico", Toast.LENGTH_LONG).show();
		}
		else if (code == Constants.REPLY_TYPE_FUEL) { // Low fuel warning
		 	
		 	runOnUiThread(new MessageRunnable(getText(R.string.reply_message_fuel).toString() ,useVerbosity, Constants.VIEW_BOTH));
		 	
		 	if (isConnected()) {
		 		useChat(Constants.TYPE_PROBLEM_FUEL); // send now request of fuel list
		 	}
		 	else {
		 		myReplyTimer = new Timer();
		 		myReplyTimer.schedule(new TimerTask() {
		 			@Override
		 			public void run() {
		 				timerReplyMethod(); // retry send request of fuel list
		 			}
		 		}, Constants.REPLY_INITIAL_DELAY_FOR_START_SENDING, Constants.REPLY_FREQUENCY);
		 	}
		}
	}
	
	private void timerReplyMethod(){
		this.runOnUiThread(sendWarningReply);
	}

	private Runnable sendWarningReply = new Runnable() {
		public void run() {
			useChat(Constants.TYPE_PROBLEM_FUEL);
			myReplyTimer.cancel();
		}
	};
	
	@Override
	public void onNewIntent(Intent intent) {
		// Analyze Intent from Reply APP
		if (intent != null) {
			int code = intent.getIntExtra(Constants.REPLY_ITEM_KEY, -1);
			analyzeCodeReply(code);
		}
		super.onNewIntent(intent);
	}
    
    private void initialCommonsOperations() {    
        buttonTraffic = (Button)this.findViewById(R.id.buttonTraffic);
        buttonTraffic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useChat(Constants.TYPE_TRAFFIC);
			}
        });
        
        buttonWeather = (Button)this.findViewById(R.id.buttonWeather);
        buttonWeather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useChat(Constants.TYPE_WEATHER);
			}
        });
	
        buttonCrash = (Button)this.findViewById(R.id.buttonCrash);
        buttonCrash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useChat(Constants.TYPE_CRASH);
			}
        });
        
        buttonMessage = (Button)this.findViewById(R.id.buttonMessage);
        buttonMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(),InsertTextActivity.class);
				startActivityForResult(i,Constants.REQUEST_CODE_INSERT_TEXT);
			}
        });
    } 
    
    private void createDialog(String messageToDisplay, boolean cancelable) {
    	try {
    		dialog = new ProgressDialog(this);
	        dialog.setMessage(messageToDisplay);
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(cancelable);
	        
	        if (cancelable) {
		        dialog.setOnCancelListener(new OnCancelListener() {
		        	@Override
		        	public void onCancel(DialogInterface dialog) {
		        		if (connection != null) {
		        			connection.disconnect();
		        			packetFilterAlreadyCreated = false;
		        			Log.i(TAG,getText(R.string.connection_closed).toString());
		        		  	runOnUiThread(new MessageRunnable(getText(R.string.connection_closed).toString(), useVerbosity, Constants.VIEW_LOG));
		        			operationIsInterruped = true;
		        			if (presencesHandler != null) {
		        				// Remove any pending posts of Runnable r that are in the message queue. 
		        				presencesHandler.removeCallbacks(updateTimeTask);
		        				presencesHandler = null; 
		        			}
		        		}
		        	}
		        });
	        }
    	}
    	catch (Exception e) {
    		Log.i(TAG, "Error creating dialog: "+e.getMessage());
    	}
    }
    
    private void initialOperationsOkNetwork() {
        createDialog(getString(R.string.text_executing), true);
        
        if (presencesHandler == null) {
        	presencesHandler = new Handler();
        }
        if (connectionHandler == null) {
        	connectionHandler = new Handler();
        }
        
        // open connection at XMPP server
        startLongRunningOperation();
        
        if (sendPresences) {
        	// Timer to send periodically presences
            updateTime();	
        }
        
        // Remove any pending posts of Runnable r that are in the message queue. 
        presencesHandler.removeCallbacks(updateTimeTask);
        	        
        // Causes the Runnable r to be added to the message queue, to be run after the specified amount of time elapses. 
        // The runnable will be run on the thread to which this handler is attached.
        if (sendPresences) {
        	presencesHandler.postDelayed(updateTimeTask, presencesDelay);
        }
    } 
    
    private void initialOperationsNoNetwork() {
    	// If network is NOT available I disable UI components    	
    	buttonTraffic.setEnabled(false);
    	buttonWeather.setEnabled(false);
    	buttonCrash.setEnabled(false);
    	buttonMessage.setEnabled(false);
    	bClearNotifications.setEnabled(false);
    	bClearLog.setEnabled(false);
    	
    	runOnUiThread(new MessageRunnable(getText(R.string.network_not_available).toString(), useVerbosity, Constants.VIEW_BOTH));
    } 
    
    public boolean isOnline() {
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	if (netInfo != null && netInfo.isConnected()) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    private class ConnectivityReceiver extends BroadcastReceiver{
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		NetworkInfo netInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
    		if (netInfo != null && netInfo.isConnected()) { // network available
    			networkAvailable = true;
    			buttonTraffic.setEnabled(true);
    			buttonWeather.setEnabled(true);
    			buttonCrash.setEnabled(true);
    			buttonMessage.setEnabled(true);
    			bClearNotifications.setEnabled(true);
    			bClearLog.setEnabled(true);
    			initialOperationsOkNetwork();	
        	}
        	else { // network NOT available
        		networkAvailable = false;
       	    	initialOperationsNoNetwork();
       	    	cleaningOperations();
        	}
    	}
    }
    
    private void cleaningOperations() {
    	// disconnect
    	if (connection != null) {
			if (connection.isConnected()) {
				connection.disconnect();
			}
			connection = null;
		}
    	
		if (presencesHandler != null) {
			// Remove any pending posts of Runnable r that are in the message queue. 
			presencesHandler.removeCallbacks(updateTimeTask);
			presencesHandler = null; 
		}
		
		if (mlocManager != null) {
			// Removes any current registration for location updates of the current activity with the given 
			// LocationListener. Following this call, updates will no longer occur for this listener.
			if (mlocListener != null) {
				mlocManager.removeUpdates(mlocListener);
			}
			mlocListener = null;
			mlocManager = null;
		}
		
		if (myReplyTimer != null) {
			myReplyTimer.cancel();
			myReplyTimer = null;
		}
	 }
   
    /**
     * Extract parameters from preferences file. Use defaul values if errors occurred.
     * @return Result
     */
    private Result allGpsParametersFound() {
    	Result resultToReturn = new Result();
    	resultToReturn.setErrorFound(false);
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    	
    		// use GPS
    		useGps = prefs.getBoolean(getText(R.string.gps_use_gps_key).toString(), true);
    		
    		// min time
    		String stringMinTime = prefs.getString(getText(R.string.gps_min_time_key).toString(), Constants.DEFAULT_STRING_VALUE);
    		if (stringMinTime.equals(Constants.DEFAULT_STRING_VALUE)) { // field is empty
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.gps_min_time_message).toString());
    			gpsMinTime = Constants.GPS_MIN_TIME; // use default
    			return resultToReturn;
    		}
    		else {
    			try {
    				gpsMinTime = Integer.parseInt(stringMinTime);
    			} 
    			catch (NumberFormatException e) {
    				resultToReturn.setErrorFound(true);
        			resultToReturn.setErrorDescription(getText(R.string.gps_min_time_message).toString());
        			gpsMinTime = Constants.GPS_MIN_TIME; // use default
        			return resultToReturn;
    			}
    		}
    		
    		// min distance
    		String stringMinDistance = prefs.getString(getText(R.string.gps_min_distance_key).toString(), Constants.DEFAULT_STRING_VALUE);
    		if (stringMinDistance.equals(Constants.DEFAULT_STRING_VALUE)) { // field is empty
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.gps_min_distance_message).toString());
    			gpsMinDistance = Constants.GPS_MIN_DISTANCE; // use default
    			return resultToReturn;
    		}
    		else {
    			try {
    				gpsMinDistance = Integer.parseInt(stringMinDistance);
    			} 
    			catch (NumberFormatException e) {
    				resultToReturn.setErrorFound(true);
        			resultToReturn.setErrorDescription(getText(R.string.gps_min_distance_message).toString());
        			gpsMinDistance = Constants.GPS_MIN_DISTANCE; // use default
        			return resultToReturn;
    			}
    		}
    		
    		// display coordinates to user
    		displayCoordinates = prefs.getBoolean(getText(R.string.gps_display_coordinates_key).toString(), true);
    		
    	}
    	catch (Exception e) {
    		resultToReturn.setErrorFound(true);
			resultToReturn.setErrorDescription(e.getMessage());
			return resultToReturn;
    	}

		return resultToReturn;
    }
	
    /**
     * Extract parameters from preferences file. Use defaul values if errors occurred.
     * @return Result
     */
    private Result allPresencesParametersFound() {
    	Result resultToReturn = new Result();
    	resultToReturn.setErrorFound(false);
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    	
    		// send Presences
    		sendPresences = prefs.getBoolean(getText(R.string.presences_send_presences_key).toString(), true);
    		
    		// frequency
    		String stringFrequency = prefs.getString(getText(R.string.presences_frequency_key).toString(), Constants.DEFAULT_STRING_VALUE);
    		if (stringFrequency.equals(Constants.DEFAULT_STRING_VALUE)) { // field is empty
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.presences_frequency_message).toString());
    			presencesFrequency = Constants.PRESENCES_FREQUENCY; // use default
    			return resultToReturn;
    		}
    		else {
    			try {
    				presencesFrequency = Integer.parseInt(stringFrequency);
    			} 
    			catch (NumberFormatException e) {
    				resultToReturn.setErrorFound(true);
        			resultToReturn.setErrorDescription(getText(R.string.presences_frequency_message).toString());
        			presencesFrequency = Constants.PRESENCES_FREQUENCY; // use default
        			return resultToReturn;
    			}
    		}
    		
    		// delay
    		String stringDelay = prefs.getString(getText(R.string.presences_initial_delay_key).toString(), Constants.DEFAULT_STRING_VALUE);
    		if (stringDelay.equals(Constants.DEFAULT_STRING_VALUE)) { // field is empty
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.presences_initial_delay_message).toString());
    			presencesDelay = Constants.PRESENCES_INITIAL_DELAY_FOR_START_SENDING; // use default
    			return resultToReturn;
    		}
    		else {
    			try {
    				presencesDelay = Integer.parseInt(stringDelay);
    			} 
    			catch (NumberFormatException e) {
    				resultToReturn.setErrorFound(true);
        			resultToReturn.setErrorDescription(getText(R.string.presences_initial_delay_message).toString());
        			presencesDelay = Constants.PRESENCES_INITIAL_DELAY_FOR_START_SENDING; // use default
        			return resultToReturn;
    			}
    		}    		
    	}
    	catch (Exception e) {
    		resultToReturn.setErrorFound(true);
			resultToReturn.setErrorDescription(e.getMessage());
			return resultToReturn;
    	}

		return resultToReturn;
    }
   
    /**
     * Checking for parameters required to connect to remote server XMPP.
     * Return Result, which specifies the presence of an error and the possible description.
     * @return Result
     */
    private Result allParametersFound() {
    	Result resultToReturn = new Result();
    	resultToReturn.setErrorFound(false);
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	try {
    		// server
    		REMOTE_SERVER_ADDRESS = prefs.getString("textServerAddress", Constants.DEFAULT_STRING_VALUE);
    		if (REMOTE_SERVER_ADDRESS.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.server_address_message).toString());
    			return resultToReturn;
    		}
    		
    		String stringPort = prefs.getString("textServerPort", Constants.DEFAULT_STRING_VALUE);
    		if (stringPort.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.server_port_message).toString());
    			return resultToReturn;
    		}
    		else {
    			try {
    				REMOTE_SERVER_PORT = Integer.parseInt(stringPort);
    			} 
    			catch (NumberFormatException e) {
    				resultToReturn.setErrorFound(true);
        			resultToReturn.setErrorDescription(getText(R.string.server_port_message).toString());
        			return resultToReturn;
    			}
    		}
    		
    		REMOTE_SERVER_NAME = prefs.getString("textServerName", Constants.DEFAULT_STRING_VALUE);
    		if (REMOTE_SERVER_NAME.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.server_name_message).toString());
    			return resultToReturn;
    		}
    		
    		REMOTE_PUBSUB_SERVICE = prefs.getString("textServerPubSubService", Constants.DEFAULT_STRING_VALUE);
    		if (REMOTE_PUBSUB_SERVICE.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.server_pubsub_message).toString());
    			return resultToReturn;
    		}
    		
    		REMOTE_RESOURCE = prefs.getString("textServerResource", Constants.DEFAULT_STRING_VALUE);
    		if (REMOTE_RESOURCE.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.server_resource_message).toString());
    			return resultToReturn;
    		}
    		
    		REMOTE_MANAGER_ACCOUNT = prefs.getString("textServerAccount", Constants.DEFAULT_STRING_VALUE);
    		if (REMOTE_MANAGER_ACCOUNT.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.server_account_message).toString());
    			return resultToReturn;
    		}
    		// client
    		CLIENT_USERNAME = prefs.getString("textClientLogin", Constants.DEFAULT_STRING_VALUE);
    		if (CLIENT_USERNAME.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.client_login_message).toString());
    			return resultToReturn;
    		}
    		
    		CLIENT_PASSWORD = prefs.getString("textClientPassword", Constants.DEFAULT_STRING_VALUE);
    		if (CLIENT_PASSWORD.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.client_password_message).toString());
    			return resultToReturn;
    		}
    		
    		CLIENT_SUBSCRIPTION = prefs.getString("listSubscriptionType", Constants.DEFAULT_STRING_VALUE);
    		
    		CLIENT_DESCRIPTION = prefs.getString("textClientDescription", Constants.DEFAULT_STRING_VALUE);
    		if (CLIENT_DESCRIPTION.equals(Constants.DEFAULT_STRING_VALUE)) {
    			resultToReturn.setErrorFound(true);
    			resultToReturn.setErrorDescription(getText(R.string.client_description_message).toString());
    			return resultToReturn;
    		}  		
    	}
    	catch (Exception e) {
    		resultToReturn.setErrorFound(true);
			resultToReturn.setErrorDescription(e.getMessage());
			return resultToReturn;
    	}

		return resultToReturn;
	}
    
    private void createGpsDisabledAlert(){  
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  
    	builder.setMessage(getText(R.string.gps_is_disabled_question).toString())
    		   .setCancelable(false)
    		   .setPositiveButton(R.string.gps_enable,  
    				   			   new DialogInterface.OnClickListener(){  
								  		public void onClick(DialogInterface dialog, int id){  
								  			showGpsOptions();  
								  		}  
    		   });  
    	builder.setNegativeButton(R.string.gps_do_nothing,  
    	             			  new DialogInterface.OnClickListener(){  
    	             					public void onClick(DialogInterface dialog, int id){  
    	             						dialog.cancel();  
    	             					}  
    		   });  
    	AlertDialog alert = builder.create();  
    	alert.show();  
    }  
    
    private void showGpsOptions(){  
    	// An intent is an abstract description of an operation to be performed. It can be used with startActivity to 
    	// launch an Activity, broadcastIntent to send it to any interested BroadcastReceiver components, and 
    	// startService(Intent) or bindService(Intent, ServiceConnection, int) to communicate with a background Service.

    	// An Intent provides a facility for performing late runtime binding between the code in different applications. 
    	// Its most	significant use is in the launching of activities, where it can be thought of as the glue between 
    	// activities. It is basically a passive data structure	holding an abstract description of an action to be performed.
    	// The primary pieces of information in an intent are:
    	//     action -- The general action to be performed, such as ACTION_VIEW, ACTION_EDIT, ACTION_MAIN, etc.
    	//	   data   -- The data to operate on, such as a person record in the contacts database, expressed as a Uri.

    	
    	Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
    	startActivity(gpsOptionsIntent);  
    }  
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
    	// This is only called once, the first time the options menu is displayed. To update the menu every time it is 
    	// displayed, see onPrepareOptionsMenu(Menu). 
    	
        MenuInflater inflater = getMenuInflater();
        // inflate menu resource (convert the XML resource into a programmable object)
        inflater.inflate(R.menu.menu, menu);
        
        if (networkAvailable) {
        	MenuItem item = menu.findItem(R.id.topic);
        	item.setEnabled(true);
        	item = menu.findItem(R.id.connection);
        	item.setEnabled(true); 
        	item = menu.findItem(R.id.gps);
        	item.setEnabled(true);
        	item = menu.findItem(R.id.settings);
        	item.setEnabled(true);
        	item = menu.findItem(R.id.about);
        	item.setEnabled(true);
        }
        else {
        	MenuItem item = menu.findItem(R.id.topic);
        	item.setEnabled(false);
        	item = menu.findItem(R.id.connection);
        	item.setEnabled(false); 
        	item = menu.findItem(R.id.gps);
        	item.setEnabled(false);
        	item = menu.findItem(R.id.settings);
        	item.setEnabled(false);
        	item = menu.findItem(R.id.about);
        	item.setEnabled(false);
        }
        
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	// Prepare the Screen's standard options menu to be displayed. This is called right before the menu is 
    	// shown, every time it is shown. You can use this method to efficiently enable/disable items or otherwise 
    	// dynamically modify the contents.
        
        if (networkAvailable) {
        	MenuItem item = menu.findItem(R.id.topic);
        	item.setEnabled(true);
        	item = menu.findItem(R.id.connection);
        	item.setEnabled(true); 
        	item = menu.findItem(R.id.gps);
        	item.setEnabled(true);
        	item = menu.findItem(R.id.settings);
        	item.setEnabled(true);
        	item = menu.findItem(R.id.about);
        	item.setEnabled(true);
        }
        else {
        	MenuItem item = menu.findItem(R.id.topic);
        	item.setEnabled(false);
        	item = menu.findItem(R.id.connection);
        	item.setEnabled(false);
        	item = menu.findItem(R.id.gps);
        	item.setEnabled(false);
        	item = menu.findItem(R.id.settings);
        	item.setEnabled(false);
        	item = menu.findItem(R.id.about);
        	item.setEnabled(false);
        }
        
        return true;
    }
  
    public void showGpsSettings() {
    	try {
    		Intent intent = new Intent(this, PreferencesGpsActivity.class);
   		 	startActivityForResult(intent,Constants.REQUEST_CODE_GPS);
   	 	}
   	 	catch (Exception e) {
   	 		Log.i(TAG,"Error showing GPS settings:"+e.getMessage());
   	 	}
    }
    
    public void showConnectionSettings() {
    	try {
    		Intent intent = new Intent(this, PreferencesConnectionActivity.class);
    		
    		// Launch an activity for which you would like a result when it finished. When this activity exits, 
    		// your onActivityResult() method will be called with the given requestCode. Using a negative requestCode 
    		// is the same as calling startActivity(Intent) (the activity is not launched as a sub-activity).
    		// Note that this method should only be used with Intent protocols that are defined to return a result. 
    		// In other protocols (such as ACTION_MAIN or ACTION_VIEW), you may not get the result when you expect. 
    		// For example, if the activity you are launching uses the singleTask launch mode, it will not run in your
    		// task and thus you will immediately receive a cancel result.
    		// As a special case, if you call startActivityForResult() with a requestCode >= 0 during the initial 
    		// onCreate(Bundle savedInstanceState)/onResume() of your activity, then your window will not be displayed until 
    		// a result is returned back from the started activity. This is to avoid visible flickering when redirecting to 
    		// another activity.
   		 	startActivityForResult(intent, Constants.REQUEST_CODE_CONNECTION);
   	 	}
   	 	catch (Exception e) {
   	 		Log.i(TAG,"Error showing Connection settings:"+e.getMessage());
   	 	}
    }
    
    public void showTopicSettings() {
    	try {
    		Intent intent = new Intent(this, PreferencesTopicActivity.class);
   		 	startActivityForResult(intent,Constants.REQUEST_CODE_TOPIC);
   	 	}
   	 	catch (Exception e) {
   	 	Log.i(TAG,"Error showing Topic settings:"+e.getMessage());
   	 	}
    }
    
    private boolean isTopicSubscribed(String topicKey) {
    	boolean subscribeNode = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		subscribeNode = prefs.getBoolean(topicKey, true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking Topic status:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return subscribeNode;
    }

    public void showApplicationSettings() {
    	try {
    		Intent intent = new Intent(this, PreferencesSettingsActivity.class);
   		 	startActivityForResult(intent,Constants.REQUEST_CODE_SETTINGS);
   	 	}
   	 	catch (Exception e) {
   	 		Log.i(TAG,"Error showing Applications settings:"+e.getMessage());
   	 	}
    }
    
    public void showAbout() {
    	try {
    		Intent intent = new Intent(this, AboutActivity.class);
   		 	startActivity(intent);
   	 	}
   	 	catch (Exception e) {
   	 		Log.i(TAG,"Error showing About Activity:"+e.getMessage());
   	 	}
    }
    
    private boolean useGpsInApplication() {
    	boolean useGps = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		useGps = prefs.getBoolean(getText(R.string.gps_use_gps_key).toString(), true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking use of GPS in application:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return useGps;
    }
    
    private void configureGpsAnalyzingPreferencesFile() {
    	runOnUiThread(new Runnable(){
		public void run() {
			useGps = useGpsInApplication();
			if (useGps) {
				Result resultRetrieved = allGpsParametersFound(); 
		    	
    			if (resultRetrieved.isErrorFound()) { // NOT all parameters found, I must use default values!
    				// display message at user
    				runOnUiThread(new MessageRunnable(resultRetrieved.getErrorDescription()+"."+
    												  getText(R.string.use_default_values).toString(),
    												  useVerbosity,Constants.VIEW_BOTH));
    				// restore default values
    				gpsMinTime = Constants.GPS_MIN_TIME;
    				gpsMinDistance = Constants.GPS_MIN_DISTANCE;
    			}	
				
    			if (mlocManager == null) { // LocationManager not found
    				// 	Use the LocationManager class to obtain GPS locations. 
    				mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    			}
    			
    			if (mlocListener == null) { // LocationListener not found
    				// Now we need add a location listener, so each time the GPS senses a new location, 
    				// this callback function will be called from the android system.
    				mlocListener = new MyLocationListener();
    			}
                /* Registers the current activity to be notified periodically by the named provider. Periodically, 
                   the supplied LocationListener will be called with the current Location or with status updates.

        		   It may take a while to receive the most recent location. If an immediate location is required, 
        		   applications may use the getLastKnownLocation(String) method.

        		   In case the provider is disabled by the user, updates will stop, and the onProviderDisabled(String) 
        		   method will be called. As soon as the provider is enabled again, the onProviderEnabled(String) method 
        		   will be called and location updates will start again.

        		   The frequency of notification may be controlled using the minTime and minDistance parameters. If minTime 
        		   is greater than 0, the LocationManager could potentially rest for minTime milliseconds between location 
        		   updates to conserve power. 
        		   If minDistance is greater than 0, a location will only be broadcasted if the device moves by minDistance 
        		   meters. 
        		   To obtain notifications as frequently as possible, set both parameters to 0.

        		   Background services should be careful about setting a sufficiently high minTime so that the device doesn't
        		   consume too much power by keeping the GPS or wireless radios on all the time. In particular, values under 
        		   60000ms are not recommended.

        		   The calling thread must be a Looper thread such as the main thread of the calling Activity.
        		   Parameters
        				provider 	the name of the provider with which to register
        				minTime 	the minimum time interval for notifications, in milliseconds. This field is only used as a hint to conserve power, and actual time between location updates may be greater or lesser than this value.
        				minDistance 	the minimum distance interval for notifications, in meters
        				listener 	a {#link LocationListener} whose onLocationChanged(Location) method will be called for each location update
                 */
    			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsMinTime, gpsMinDistance, mlocListener);
    			
    			if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
    				createGpsDisabledAlert();  
    			}
			}
			else { // I don't want use GPS functionalities. I dont't STOP/START GPS (others applications could use it)
				if (mlocManager != null) {
					// Removes any current registration for location updates of the current activity with the given 
					// LocationListener. Following this call, updates will no longer occur for this listener.
					mlocManager.removeUpdates(mlocListener);
					mlocManager = null;
				}
			}
		}
	});	
    }
    
    private void configureTopicAnalyzingPreferencesFile() {
    	subscribeAir = isTopicSubscribed(getText(R.string.topic_air_key).toString());
    	subscribeAlert = isTopicSubscribed(getText(R.string.topic_alert_key).toString());
    	subscribeNews = isTopicSubscribed(getText(R.string.topic_news_key).toString());
    	subscribeParking = isTopicSubscribed(getText(R.string.topic_parking_key).toString());
    	subscribeRoad = isTopicSubscribed(getText(R.string.topic_road_key).toString());
    	subscribeTraffic = isTopicSubscribed(getText(R.string.topic_traffic_key).toString());
    	subscribeWeather = isTopicSubscribed(getText(R.string.topic_weather_key).toString());
    }
    
    private boolean checkSendPresences() {
    	boolean sendPresences = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		sendPresences = prefs.getBoolean(getText(R.string.presences_send_presences_key).toString(), true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking sending presences in application:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return sendPresences;
    }
    
    private void configureSettingsAnalyzingPreferencesFile() {
    	runOnUiThread(new Runnable(){
		public void run() {
			useVerbosity = useVerbosityInApplication();
			useAlert = useAlertInApplication();
			sendToAll = checkSendToAll();
			sendPresences = checkSendPresences();
			displayMarkers = displayMarkerInApplication();
			resultsNumber = getResultsNumberFromPreferences();
			useFakeCoordinates = useFakeCoordinatesInApplication();
			
			if (sendPresences) {
				Result resultRetrieved = allPresencesParametersFound(); 
		    	
    			if (resultRetrieved.isErrorFound()) { // NOT all parameters found, I must use default values!
    				// display message at user
    				runOnUiThread(new MessageRunnable(resultRetrieved.getErrorDescription()+"."+
    												  getText(R.string.use_default_values).toString(),
    												  useVerbosity,Constants.VIEW_BOTH));
    				// restore default values
    				presencesFrequency = Constants.PRESENCES_FREQUENCY;
    				presencesDelay = Constants.PRESENCES_INITIAL_DELAY_FOR_START_SENDING;
    			}	
			}
		}
	});	
    }
    
    private int getResultsNumberFromPreferences() {
    	int result = Constants.RESULTS_NUMBER_FROM_SERVER; // use default
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    		String stringResultsNumber = prefs.getString(getText(R.string.results_number_key).toString(), Constants.DEFAULT_STRING_VALUE);
    		if (!stringResultsNumber.equals(Constants.DEFAULT_STRING_VALUE)) { // field is not empty
    			result = Integer.parseInt(stringResultsNumber);
    		}
    	}	
    	catch (NumberFormatException e) {
    		Log.i(TAG,"Error checking number of results in application:"+e.getMessage());
    	}
		return result;
    }
    
	private boolean useVerbosityInApplication() {
    	boolean useVerbosity = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		useVerbosity = prefs.getBoolean(getText(R.string.verbosity_key).toString(), true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking use of Verbosity in application:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return useVerbosity;
    }

	private boolean useFakeCoordinatesInApplication() {
    	boolean useFakeCoordinates = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		useFakeCoordinates = prefs.getBoolean(getText(R.string.fake_coordinates_key).toString(), true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking use of Fake Coordinates in application:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return useFakeCoordinates;
    }
	
    private boolean useAlertInApplication() {
    	boolean useAlert = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		useAlert = prefs.getBoolean(getText(R.string.verbosity_alert_key).toString(), true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking use of Alert in application:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return useAlert;
    }
    
    private boolean displayMarkerInApplication() {
    	boolean display = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		display = prefs.getBoolean(getText(R.string.verbosity_marker_key).toString(), true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking use of Markers in application:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return display;
    }
    
    private boolean checkSendToAll() {
    	boolean sendToAll = true;
    	
    	try {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);	
    		sendToAll = prefs.getBoolean(getText(R.string.messages_to_near_key).toString(), true);
    	}
    	catch (Exception e) {
    		Log.i(TAG,"Error checking send to all/to near flag:"+e.getMessage());
    		// return true if there is an error
			return true;
    	}

		return sendToAll;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == Constants.REQUEST_CODE_TOPIC) {
            if (resultCode == RESULT_CANCELED) { // pressed BACK key
            	configureTopicAnalyzingPreferencesFile();
            	managePubSubNodes();
            }
        }
    	else if (requestCode == Constants.REQUEST_CODE_CONNECTION) {
            if (resultCode == RESULT_CANCELED) { // pressed BACK key
            	operationIsInterruped = false;
            	startLongRunningOperation(); // connection xmpp
            }
        }
        else if (requestCode == Constants.REQUEST_CODE_GPS) {
            if (resultCode == RESULT_CANCELED) { // pressed BACK key
            	configureGpsAnalyzingPreferencesFile();
            }
        }
        else if (requestCode == Constants.REQUEST_CODE_SETTINGS) {
            if (resultCode == RESULT_CANCELED) { // pressed BACK key
            	configureSettingsAnalyzingPreferencesFile();
            	if (isConnected()) {
        			sendReceiversSetting(); // I send Chat Message to server for configure Sento to All/Only to Near 
        		}
            	managePresencesTimer();
            }
        }
        else if (requestCode == Constants.REQUEST_CODE_INSERT_TEXT) {
        	analyzeResultFromInsertTextActivity(resultCode, data);
        }
    }
        
    private void managePresencesTimer() {
    	if (sendPresences) { // I must start Timer if NOT running / update timer with new values
    		if (presencesHandler == null) {
    			presencesHandler = new Handler();
    		}
    		else {
    			// Remove any pending posts of Runnable r that are in the message queue. 
    	        presencesHandler.removeCallbacks(updateTimeTask);
    		}
    	     presencesHandler.postDelayed(updateTimeTask, presencesDelay);  
    	}
    	else { // I must stop Timer if running
    		if (presencesHandler != null) {
    			// Remove any pending posts of Runnable r that are in the message queue. 
    			presencesHandler.removeCallbacks(updateTimeTask);
    			presencesHandler = null; 
    		}
    	}
	}

	private void analyzeResultFromInsertTextActivity(int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) { // pressed "Send"
    		String messageInserted = data.getExtras().getString("valueInserted");
    		sendInsertedMessage(messageInserted);
        }
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.topic:
        		showTopicSettings();
    		return true;
        	case R.id.connection:
        		showConnectionSettings();
        		return true;
        	case R.id.gps:
        		showGpsSettings();
        		return true;
        	case R.id.settings:
        		showApplicationSettings();
        		return true;
        	case R.id.about:
        		showAbout();
        		return true;	
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Class that implements LocationListener, which listens for both changes in the location of the
     * device and changes in the status of the GPS system.
     */
    public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			Double lat = location.getLatitude();
			Double lon = location.getLongitude();
			
			latitudeToSent = lat;
			longitudeToSent = lon;

			if (displayCoordinates) {
				String text = "Lat:"+lat+" Lon:"+lon;
				runOnUiThread(new MessageRunnable(text,useVerbosity,Constants.VIEW_LOG));
			}
			
			if (displayMarkers) {
				int latInt = (int) Math.floor(lat*1.0E6);
				int longInt = (int) Math.floor(lon*1.0E6);
				
				GeoPoint point = new GeoPoint(latInt, longInt);
				
				// center map on new coordinates
				centerMap(point);

				// Reverse geocoding
				String address = reverseGeocoding(point);
				
				// add overlay
				addOverlay(point, location.getAccuracy(), getResources().getString(R.string.map_current_position), address, Constants.TYPE_NORMAL);
			}	
		}

		@Override
		public void onProviderDisabled(String provider) {
			runOnUiThread(new MessageRunnable(getText(R.string.gps_disabled).toString(),useVerbosity,Constants.VIEW_LOG));
			latitudeToSent = -1.0;
			longitudeToSent = -1.0;
		}

		@Override
		public void onProviderEnabled(String provider) {
			runOnUiThread(new MessageRunnable(getText(R.string.gps_enabled).toString(),useVerbosity,Constants.VIEW_LOG));
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
    }
    
    /**
     * Centers the map to the point
     * 
     * @param point
     */
    public void centerMap(GeoPoint point) {
    	mapController.animateTo(point);
    }
    
    /**
     * Get the address from the coordinate
     * 
     * @param point
     * @return address
     */
    public String reverseGeocoding(GeoPoint point) {
    	Geocoder geocoder = new Geocoder(this);
    	String addressString = "";
		try {
			List<Address> addressList = geocoder.getFromLocation(point.getLatitudeE6()/(double)1E6, point.getLongitudeE6()/(double)1E6, 1);
			if (addressList.size() != 0) {
				Address address = addressList.get(0);
				for (int i=0; i<address.getMaxAddressLineIndex(); i++) {
					addressString += address.getAddressLine(i) + "\n";
				}
			}			
		} 
		catch (Exception e) {
			Log.i(TAG, "No address retrieved from Reverse Geocoding");
		}
		return addressString;
    }
    
    /**
     * Add a custom overlay to the map
     * 
     * @param point GeoPoint where centre the overlay
     * @param accuracy accuracy of the location
     * @param address of the location
     */
    public void addOverlay(GeoPoint point, float accuracy, String titleToDispaly, String infoToDisplay, int icon) {
		MyOverlayItem myOverlayItem = new MyOverlayItem(point, accuracy, titleToDispaly, infoToDisplay, icon, getBaseContext());
		// add overlay to list
		myOverlays.addOverlay(myOverlayItem);
		// display on map
		mapView.getOverlays().add(myOverlays);
    }
    
    public void sendPresence() {
    	if (isConnected() && (sendPresences)) {
			//Double latitude = 45.1029;
			//Double longitude = 7.5277;
			
			if ((latitudeToSent != -1.0) && (longitudeToSent != -1.0)) { // location found
				Log.i(TAG,"Send "+latitudeToSent+"*"+longitudeToSent);				
				Presence presence = new Presence(Presence.Type.available);
				presence.setStatus(latitudeToSent+"*"+longitudeToSent);
				connection.sendPacket(presence);
			}
			else {
				if (useFakeCoordinates) { // anche se non sono presenti coordinate, se il flag demo è a true, invio le coordinate fittizie
					Log.i(TAG,"Send "+Constants.FAKE_LAT+"*"+Constants.FAKE_LON);	
					Presence presence = new Presence(Presence.Type.available);
					presence.setStatus(Constants.FAKE_LAT+"*"+Constants.FAKE_LON);
					connection.sendPacket(presence);
				}
				else {
					Log.i(TAG,getText(R.string.location_not_available).toString());
					Presence presence = new Presence(Presence.Type.available);
					presence.setStatus(Constants.NO_LOCATION_AVAILABLE);
					connection.sendPacket(presence);
				}
			}
		}
	}
	
	class MessageRunnable implements Runnable {
		private String message = "";
		private boolean moreVerbose = true;
		private int type = Constants.VIEW_BOTH; //default
		
		public MessageRunnable(String message, boolean moreVerbose, int type) {
			this.message = message;
			this.moreVerbose = moreVerbose;
			this.type = type;
		}
		
		@Override
		public void run() {
			if (type == Constants.VIEW_NOTIFICATIONS) {
				writeToNotifications(message,  moreVerbose);	
			}
			else if (type == Constants.VIEW_LOG) {
				writeToLog(message, moreVerbose);
			}
			else if (type == Constants.VIEW_BOTH) {
				writeToNotifications(message, moreVerbose);
				writeToLog(message, moreVerbose);
			}
		}
	}
	
	class DialogRunnable implements Runnable {
		private String dialogTitle = "";
		private String message = "";
		private int icon = R.string.dialog_ok;
		private String textButton = "";
		
		public DialogRunnable(String dialogTitle, String message, int icon, String textButton) {
			this.dialogTitle = dialogTitle;
			this.message = message;
			this.icon = icon;
			this.textButton = textButton;
		}
		
		@Override
		public void run() {
			showCustomDialog(dialogTitle,message,icon,textButton);
		}
	}

	class DialogFuelRunnable implements Runnable {
		private String dialogTitle = "";
		private String message = "";
		private int icon = R.string.dialog_ok;
		private String textButton = "";
		
		public DialogFuelRunnable(String dialogTitle, String message, int icon, String textButton) {
			this.dialogTitle = dialogTitle;
			this.message = message;
			this.icon = icon;
			this.textButton = textButton;
		}
		
		@Override
		public void run() {
			showCustomDialogFuel(dialogTitle,message,icon,textButton);
		}
	}
	
	private synchronized  void startInitOperations(){
    	Result resultRetrieved = allParametersFound(); 
    	
    	if (!resultRetrieved.isErrorFound()) { 	// All parameters found, try to access into server
    		if (connection != null) { // there was already an previous open connection
    			connection.disconnect();
    			packetFilterAlreadyCreated = false;
    			Log.i(TAG,getText(R.string.connection_closed).toString());
    			runOnUiThread(new MessageRunnable(getText(R.string.connection_closed).toString(), useVerbosity, Constants.VIEW_BOTH));
    		}
    		
    		Log.i(TAG,getText(R.string.connection_starting).toString());
    		runOnUiThread(new MessageRunnable(getText(R.string.connection_starting).toString(), useVerbosity, Constants.VIEW_LOG));
    		
    		config = new ConnectionConfiguration(REMOTE_SERVER_ADDRESS, REMOTE_SERVER_PORT);
    		config.setDebuggerEnabled(true);
    		config.setSASLAuthenticationEnabled(false);
        
    		Log.i(TAG,"host:"+ config.getHost());
    		Log.i(TAG,"getKeystorePath:"+ config.getKeystorePath());
    		Log.i(TAG,"config.getKeystoreType:"+ config.getKeystoreType());
        
    		connection = new XMPPConnection(config);
    		
    		boolean userConnected = false;
    	
    		// Try to connect. If there is an Exception try to create the account
    		try {
    			connection.connect();
    			Log.i(TAG,"user connected:"+ connection.isConnected());
    			Log.i(TAG,"user authenticated:"+ connection.isAuthenticated());
    			connection.login(CLIENT_USERNAME, CLIENT_PASSWORD, REMOTE_RESOURCE);
    			userConnected = true;
    		} 
    		catch (XMPPException e) {
    			if(e.getWrappedThrowable()==null) {
    				AccountManager accountManager = connection.getAccountManager();
    				Map <String,String> managerProps = new HashMap<String, String>();
    				managerProps.put("name", CLIENT_DESCRIPTION);
    				try {
    					// Create client account
    					accountManager.createAccount(CLIENT_USERNAME, CLIENT_PASSWORD, managerProps);
    					connection.disconnect();
    					connection.connect();
    					connection.login(CLIENT_USERNAME, CLIENT_PASSWORD,REMOTE_RESOURCE);
    					// Set subscription type
    					Roster roster = connection.getRoster();
    					if(CLIENT_SUBSCRIPTION.equals(getText(R.string.subscription_accept_all).toString()))
    						roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
    					else if(CLIENT_SUBSCRIPTION.equals(getText(R.string.subscription_reject_all).toString()))
    						roster.setSubscriptionMode(Roster.SubscriptionMode.reject_all);
    					else if(CLIENT_SUBSCRIPTION.equals(getText(R.string.subscription_manual).toString()))
    						roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
    					else
    						roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
			        
    					// create roster group
    					//String [] groups = {"config"};
			        
    					// Creates a new roster entry and presence subscription. The server will asynchronously 
    					// update the roster with the subscription status. 
					
    					// NOTE! Add into Roster "manager_dispositivi_thomas"
			        
    					// Creates a new roster entry and presence subscription. The server will asynchronously update
    					// the roster with the subscription status.
    					// Parameters:
    					//    			user - the user. (e.g. johndoe@jabber.org)
    					//   			name - the nickname of the user.
    					//   			groups - the list of group names the entry will belong to, or null if the the 
    					//						 roster entry won't belong to a group. 
    					roster.createEntry(REMOTE_MANAGER_ACCOUNT+"@"+REMOTE_SERVER_NAME, 
    									   getText(R.string.user_manager_description).toString(),
    									   null);
    					userConnected = true;
    				} 
    				catch (XMPPException e1) {
    					userConnected = false;
    					Log.i(TAG,"Error creating roster entry. Error:"+e1.getMessage());
    				}
    				catch (Exception e2) {
    					userConnected = false;
    					Log.i(TAG,"Error during connection. Error:"+e2.getMessage());
    				}
    			}
    		}
    		catch (Exception ex) {
    			userConnected = false;
    			Log.i(TAG,"Error during connection. Error:"+ex.getMessage());
    		}
		
    		if (userConnected) {
    			Log.i(TAG,getText(R.string.connection_completed).toString());
    			runOnUiThread(new MessageRunnable(getText(R.string.connection_completed).toString(), useVerbosity, Constants.VIEW_LOG));
    			runOnUiThread(new MessageRunnable(getText(R.string.user_connected).toString(),useVerbosity, Constants.VIEW_BOTH));
    		}
    		else {
    			Log.i(TAG,getText(R.string.user_not_connected).toString());
    			runOnUiThread(new MessageRunnable(getText(R.string.user_not_connected).toString(),useVerbosity, Constants.VIEW_BOTH));
    			return;
    		}	
    	
	    	// Presence(Presence.Type type, String status, int priority, Presence.Mode mode)
	    	//
	    	// Creates a new presence update with a specified status, priority, and mode.
	    	//
	    	// Parameters:
	        //		type - the type.
	        //		status - a text message describing the presence update.
	        //		priority - the priority of this presence update.
	        //		mode - the mode type for this presence update.

    		connection.sendPacket(new Presence(Presence.Type.available,
    										   getText(R.string.app_title).toString(),
    										   Constants.PRESENCE_PRIORITY,
    										   Presence.Mode.available));		      
    		boolean error = false;
    		do {
    			Roster roster = connection.getRoster();		
    			if (roster==null) {
    				error = true;
    			}
    			if (!error) {
    				// add rosterListener to receive change events for this roster
    				roster.addRosterListener(this);
    			}
    		} while(error);
    	
    		// I want capture Presence
    		PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);
        
    		// Add packet listener to capture requests of roster addition
    		PacketListener packetListener = new PacketListener() {
    			public void processPacket(Packet packet) {
    				Presence presence = (Presence) packet;
    				if (presence.getType() == Presence.Type.subscribe) { // another user want add account into own roster
    					Presence answerPresence = new Presence(Presence.Type.subscribe);
    					// Sets who the packet is being sent "to". The XMPP protocol often makes the "to" attribute 
    					// optional, so it does not always need to be set. 
    					answerPresence.setTo(presence.getFrom());
    					connection.sendPacket(answerPresence);
		
    					Roster roster = connection.getRoster();
    					try {
    						roster.createEntry(presence.getFrom(), presence.getFrom(), null); // add entry (no group)
    					} 
    					catch (XMPPException e1) {
    						Log.i(TAG,"Error adding entry in roster. Error:"+e1.getMessage());
    						runOnUiThread(new MessageRunnable(getText(R.string.error_adding_entry_in_roster).toString(),useVerbosity, Constants.VIEW_LOG));
    						return;
    					}
    					catch (Exception e2) {
    						Log.i(TAG,"Error adding entry in roster. Error:"+e2.getMessage());
    						runOnUiThread(new MessageRunnable(getText(R.string.error_adding_entry_in_roster).toString(),useVerbosity, Constants.VIEW_LOG));
    						return;
    					}
    				}
    			}
    		};
    		connection.addPacketListener(packetListener, presenceFilter);
        
    		initializeASmackXEP();	
    		managePubSubNodes();
    		
    		if (isConnected()) {
    			sendReceiversSetting(); // send Chat Message for configure message to all/only to near
    		}
    	}
    	else { // missing parameters
    		Log.i(TAG,getText(R.string.server_parameters_missing).toString()+resultRetrieved.getErrorDescription());
			runOnUiThread(new MessageRunnable(getText(R.string.server_parameters_missing).toString()+resultRetrieved.getErrorDescription(),
						  useVerbosity, Constants.VIEW_BOTH));
    	}
    }

	public class MyItemEventListener implements ItemEventListener {
		private String topicKey = "";
		
		public MyItemEventListener(String topicKey) {
			this.topicKey = topicKey;
		}
		
		public String getTopicKey() {
			return topicKey;
		}

		@Override
		public void handlePublishedItems(ItemPublishEvent items) {
			// ItemPublishEvent represents an event generated by an item(s) being published to a node. 

			String titleToDisplay = getText(R.string.published_on_topic).toString()+items.getNodeId();

			Log.i(TAG,titleToDisplay); // es. Alert
			runOnUiThread(new MessageRunnable(titleToDisplay,useVerbosity,Constants.VIEW_BOTH));

			PayloadItem pi =(PayloadItem) items.getItems().get(0);
			PacketExtension pckExt = pi.getPayload();
			String info = pckExt.toXML();
			info = info.replaceAll("null", "");

			Log.i(TAG,"Info:"+info);
			runOnUiThread(new MessageRunnable("Info:"+info, useVerbosity,Constants.VIEW_BOTH));

			if (useAlert) {	
				runOnUiThread(new DialogRunnable(titleToDisplay,
						info,
						R.drawable.icon,
						getText(R.string.dialog_ok).toString()));
			}
		}
	}
	
	private void manageSinglePubSubNode(PubSubManager psm, String topic, boolean subscribeThisNode, ItemEventListener listener) {
		boolean error = false;
		Node node = null;
		
		try {
			// Retrieves the requested node, if it exists. It will throw an exception if it does not. 
			node = psm.getNode(topic);
		} 
		catch (XMPPException e) {
			error = true;
			Log.i(TAG,getText(R.string.error_getting_pubsub_node).toString()+" "+topic+"."+e.getMessage());
			runOnUiThread(new MessageRunnable(getText(R.string.error_getting_pubsub_node).toString()+" "+topic+".",
						useVerbosity, Constants.VIEW_LOG));
		}
		catch (Exception e) {
			error = true;
			Log.i(TAG,getText(R.string.error_getting_pubsub_node).toString()+" "+topic+"."+e.getMessage());
			runOnUiThread(new MessageRunnable(getText(R.string.error_getting_pubsub_node).toString()+" "+topic+".",
						useVerbosity, Constants.VIEW_LOG));
		}
		
		if (!error) {
			try {
        		try {
        			// remove previous listener
        			node.removeItemEventListener(listener);
        			// unsubscribe user
        			node.unsubscribe(connection.getUser()); //FIXME sarebbe da fare solo se lo user è già iscritto e vuole disiscriversi.
        		}
        		catch (Exception e) { // if user is not subscribed --> Exception
        		}
        		
				// The user subscribes to the node using the supplied jid. The bare jid portion of this one must match the jid for the connection.
	        	if (subscribeThisNode) {
	        		// Register a listener for item publication events. This listener will get called whenever 
	    			// an item is published to this node. 
	        		node.addItemEventListener(listener);
	        		node.subscribe(connection.getUser());
	        	}
			} 
	        catch (XMPPException e) {
	        	Log.i(TAG,getText(R.string.error_subscribing_node).toString()+e.getMessage());
			}
	        catch (Exception e) {
			}
		}		
	}
    
	private void initializeASmackXEP() {
		// Code for initialize ASmack to support XEP-0060: Publish-Subscribe
    	
    	ProviderManager pm = ProviderManager.getInstance();
        pm.addIQProvider(
            "query", "http://jabber.org/protocol/disco#items",
             new org.jivesoftware.smackx.provider.DiscoverItemsProvider()
        );
        
        pm.addIQProvider("query",
                "http://jabber.org/protocol/disco#info",
                new org.jivesoftware.smackx.provider.DiscoverInfoProvider());
        
        pm.addIQProvider("pubsub",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.PubSubProvider());

        pm.addExtensionProvider("subscription", PubSubNamespace.BASIC.getXmlns() , new SubscriptionProvider());
        
        pm.addExtensionProvider(
                "create",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());
        
        pm.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.ItemsProvider());
        
        pm.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());
        
        pm.addExtensionProvider("item", "",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());
        
        pm.addExtensionProvider(
                        "subscriptions",
                        "http://jabber.org/protocol/pubsub",
                        new org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider());

        pm.addExtensionProvider(
                        "subscriptions",
                        "http://jabber.org/protocol/pubsub#owner",
                        new org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider());

        pm.addExtensionProvider(
                        "affiliations",
                        "http://jabber.org/protocol/pubsub",
                        new org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider());
        
        pm.addExtensionProvider(
                        "affiliation",
                        "http://jabber.org/protocol/pubsub",
                        new org.jivesoftware.smackx.pubsub.provider.AffiliationProvider());
        
        pm.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());
        
        pm.addIQProvider("pubsub",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.PubSubProvider());
        
        pm.addExtensionProvider("configure",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());
        
        pm.addExtensionProvider("default",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());


        pm.addExtensionProvider("event",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.EventProvider());
        
        pm.addExtensionProvider(
                        "configuration",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider());
        
        pm.addExtensionProvider(
                        "delete",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());
        
        pm.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());
        
        pm.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.ItemsProvider());
        
        pm.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());

        pm.addExtensionProvider("headers",
                "http://jabber.org/protocol/shim",
                new org.jivesoftware.smackx.provider.HeaderProvider());

        pm.addExtensionProvider("header",
                "http://jabber.org/protocol/shim",
                new org.jivesoftware.smackx.provider.HeadersProvider());
        
        pm.addExtensionProvider(
                        "retract",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.RetractEventProvider());
        
        pm.addExtensionProvider(
                        "purge",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());
        
        pm.addExtensionProvider(
                "x",
                "jabber:x:data",
                new org.jivesoftware.smackx.provider.DataFormProvider());

        SmackConfiguration.setKeepAliveInterval(-1);
    	
		psm = new PubSubManager(connection,REMOTE_PUBSUB_SERVICE);
	}
	
	private void managePubSubNodes() {
		manageSinglePubSubNode(psm, Constants.TOPIC_AIR_POLLUTION, subscribeAir, listenerAir);
    	manageSinglePubSubNode(psm, Constants.TOPIC_ALERT, subscribeAlert, listenerAlert);
    	manageSinglePubSubNode(psm, Constants.TOPIC_PARKING, subscribeParking, listenerParking);
    	manageSinglePubSubNode(psm, Constants.TOPIC_NEWS, subscribeNews, listenerNews);
    	manageSinglePubSubNode(psm, Constants.TOPIC_ROAD_QUALITY, subscribeRoad, listenerRoad);
    	manageSinglePubSubNode(psm, Constants.TOPIC_TRAFFIC, subscribeTraffic, listenerTraffic);
    	manageSinglePubSubNode(psm, Constants.TOPIC_WEATHER, subscribeWeather, listenerWeather);
	}
    
    private Runnable updateTimeTask = new Runnable() {
    	public void run() {
    		updateTime();
    		presencesHandler.postDelayed(this, presencesFrequency);
    	}
    };

    private void updateTime() {
    	if (sendPresences) {
    		sendPresence();
    	}
    }
    
    private boolean isConnected () {
		if(connection!=null && connection.isConnected()) {
			return true;
		}	
		else {
			return false;
		}
	}
    
	@Override
	public void entriesAdded(Collection<String> arg0) {	
	}

	@Override
	public void entriesDeleted(Collection<String> arg0) {
	}

	@Override
	public void entriesUpdated(Collection<String> arg0) {
	}

	@Override
	public void presenceChanged(Presence presence) {
		if (isConnected()) {
			StringTokenizer bareJID = new StringTokenizer(presence.getFrom(),"/");
			final String jid = bareJID.nextToken();
		
			if ((jid.contains(REMOTE_MANAGER_ACCOUNT)) && (presence.isAvailable())) { // manager_dispositivi_thomas online
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 imageViewStatusServer.setImageResource(R.drawable.green);
				    }
				});
			}
			else if (jid.contains(REMOTE_MANAGER_ACCOUNT)) { // manager_dispositivi_thomas NOT online
				runOnUiThread(new Runnable() {
				     public void run() {
				    	imageViewStatusServer.setImageResource(R.drawable.red);
				    }
				});
			}
			
			Log.i(TAG,getText(R.string.received_presence).toString()+presence.getFrom()+" "+presence);
			runOnUiThread(new MessageRunnable(getText(R.string.received_presence).toString()+presence.getFrom()+" "+presence,
											  useVerbosity, Constants.VIEW_LOG));
			
			Log.i(TAG,getText(R.string.received_presence_bare_jid).toString()+jid);
			runOnUiThread(new MessageRunnable(getText(R.string.received_presence_bare_jid).toString()+jid,
					 						  useVerbosity, Constants.VIEW_LOG));
			
			if (presence.isAvailable()) {
				//if (pubSubAvailable == false) { // I must reinitialize pubsub nodes
				//	pubSubAvailable = execInit();
				//}
				
				if (packetFilterAlreadyCreated == false) {
					// if chat doesn't exist I must create new chat (account is online)
					//if (chatWithManager == null) {
						// chatmanager.createChat(receiver_user+"@"+server_name, new MessageListener()
						// chatWithManager = connection.getChatManager().createChat(jid, new MyMessageListener()); FIXME ASMACK NOTE BUG!
					new Thread(new Runnable() {
						public void run() { 
							// Accept only messages from manager_dispositivi_thomas@ripley
							PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class), 
				                                				new FromContainsFilter(jid));
			
							// Collect these messages
							PacketCollector collector = connection.createPacketCollector(filter);
		            
							packetFilterAlreadyCreated = true;
							
							while(true) {
								Packet packet = collector.nextResult();
		                
								if (packet instanceof Message) {
									final Message msg = (Message) packet;
									// Process message
									
									if (msg.getBody().contains(Constants.MSG_PROBLEM_FUEL)) { // fuel list from server
										analyzeResponseFuelList(msg.getBody());
									}
									else {
										Log.i(TAG, getText(R.string.received).toString()+msg.getBody());
										runOnUiThread(new MessageRunnable(getText(R.string.received).toString()+msg.getBody(),
																		  useVerbosity,Constants.VIEW_BOTH));
										
										if (useAlert) {	
											runOnUiThread(new DialogRunnable(getText(R.string.dialog_title).toString(),
																 			 getText(R.string.received).toString()+msg.getBody(),
																 			 R.drawable.icon,
																 			 getText(R.string.dialog_ok).toString()));
										}	
									}
								}
							}
						}
		          
					}).start();
				}	
			}
			//else { // presenza "not available"
			//	pubSubAvailable = false;
			//	removeNode();
			//}
		}
		else { // user not online
			runOnUiThread(new Runnable() {
			     public void run() {
			    	 imageViewStatusServer.setImageResource(R.drawable.red);
			     }
			});
		}
	}
	
	private void analyzeResponseFuelList(String source) {
		source = source.replace(Constants.MSG_PROBLEM_FUEL, "");
		Fuel[] fuelList = Utils.extractFuelList(source);
		String messageToDisplay = "";
		
		// exec query
		if ((fuelList != null) && (fuelList.length > 0) && (displayMarkers)) { // I have results and I want display markers
			GeoPoint[] gpArray = new GeoPoint[fuelList.length];
			// analyze  array and put marker on map			
			for (int i=0; i < fuelList.length; i++) {
			
				Fuel f = fuelList[i];
				
				int latInt = (int) Math.floor(f.getLat()*1.0E6);
				int longInt = (int) Math.floor(f.getLon()*1.0E6);
				
				GeoPoint point = new GeoPoint(latInt, longInt);
				gpArray[i] = point;
				
				// add overlay
				addOverlay(point, 0.0f, getText(R.string.dialog_title_petrol_station).toString(),f.getInfo(), Constants.TYPE_FUEL);
			}
			
			int minLatitude = Integer.MAX_VALUE;
			int maxLatitude = Integer.MIN_VALUE;
			int minLongitude = Integer.MAX_VALUE;
			int maxLongitude = Integer.MIN_VALUE;

			// Find the boundaries of the item set
			for (GeoPoint item : gpArray) { //item Contain list of Geopints
				int lat = item.getLatitudeE6();
				int lon = item.getLongitudeE6();

				maxLatitude = Math.max(lat, maxLatitude);
				minLatitude = Math.min(lat, minLatitude);
				maxLongitude = Math.max(lon, maxLongitude);
				minLongitude = Math.min(lon, minLongitude);
			}
			
			mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
			mapController.animateTo(new GeoPoint( 
												(maxLatitude + minLatitude)/2, 
												(maxLongitude + minLongitude)/2 )); 
		}	
		
		if (useAlert) {	
			if ((fuelList != null) && (fuelList.length > 0)) { // I have results
				messageToDisplay = getText(R.string.dialog_ok_result_fuel).toString();
				// display alert and after user press OK, move on TAB MAP
				runOnUiThread(new DialogFuelRunnable(getText(R.string.reply_message_fuel).toString(),
									 			 messageToDisplay,
									 			 R.drawable.image_fuel,
									 			 getText(R.string.dialog_ok).toString()));
			}
			else { // no results
				messageToDisplay = getText(R.string.dialog_no_result_fuel).toString();
				
				runOnUiThread(new DialogRunnable(getText(R.string.reply_message_fuel).toString(),
					 			 				  messageToDisplay,
					 			 				  R.drawable.image_fuel,
					 			 				  getText(R.string.dialog_ok).toString()));
			}
		}	
	}
	
	/*
	// MessageListener to manage received messages
    class MyMessageListener implements MessageListener {
    	@Override
    	public void processMessage(Chat chat, Message message) { // FIXME  ASMACK NOTE BUG!
    		String from = message.getFrom();
    		String body = message.getBody();

    		//Log.i("AutomotiveClient",message.getBody());
    		//runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody()));
    		
    		if (message.getBody()!=null) {
    			Log.i("AutomotiveClient",message.getFrom()+": "+message.getBody());
    			runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody()));
    		}
    	}		         
    }
    */
	
	/**
	 * Method to send messages via chat to manager. 
	 * @param type = message type
	 */
	public void useChat(int type) { 
		if (isConnected()) {
			ChatManager chatmanager = connection.getChatManager();
			
			String receiver_user = REMOTE_MANAGER_ACCOUNT; //manager_dispositivi_thomas
			String server_name = REMOTE_SERVER_NAME; // ripley
			
			// Create listener that prints received messages
			Chat newChat = chatmanager.createChat(receiver_user+"@"+server_name, new MessageListener()	{
				public void processMessage(Chat chat, Message message) {	
					
					Log.i(TAG,message.getBody());
					runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody(),useVerbosity, Constants.VIEW_LOG)); // FIXME ASMACK BUG
					
					if (message.getBody()!=null) {
						Log.i(TAG,message.getFrom()+": "+message.getBody());
						runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody(),useVerbosity, Constants.VIEW_LOG));
					}
				}
			});
	
			try {
				if (type == Constants.TYPE_TRAFFIC) {
					newChat.sendMessage(MSG_TRAFFIC);
					Log.i(TAG,getText(R.string.message_traffic).toString());
					runOnUiThread(new MessageRunnable(getText(R.string.message_traffic).toString() ,useVerbosity, Constants.VIEW_BOTH));
					showCustomDialog(getText(R.string.dialog_title).toString(),
										getText(R.string.message_traffic).toString(),
										R.drawable.traffic,
										getText(R.string.dialog_ok).toString());
				}
				else if (type == Constants.TYPE_WEATHER) {
					newChat.sendMessage(MSG_WEATHER);
					Log.i(TAG,getText(R.string.message_weather).toString());
					runOnUiThread(new MessageRunnable(getText(R.string.message_weather).toString() ,useVerbosity, Constants.VIEW_BOTH));
					showCustomDialog(getText(R.string.dialog_title).toString(),
										getText(R.string.message_weather).toString(),
										R.drawable.weather,
										getText(R.string.dialog_ok).toString());
				}
				else if (type == Constants.TYPE_CRASH) {
					newChat.sendMessage(MSG_CRASH);
					Log.i(TAG,getText(R.string.message_car_accident).toString());
					runOnUiThread(new MessageRunnable(getText(R.string.message_car_accident).toString() ,useVerbosity, Constants.VIEW_BOTH));
					showCustomDialog(getText(R.string.dialog_title).toString(),
										getText(R.string.message_car_accident).toString(),
										R.drawable.crash,
										getText(R.string.dialog_ok).toString());
				}
				else if (type == Constants.TYPE_PROBLEM_FUEL) {
					// message is formed by: PROBLEM_FUEL&RESULTS_NUMBER
					newChat.sendMessage(Constants.MSG_PROBLEM_FUEL+Constants.SEPARATOR_FUEL+resultsNumber);
				}
			}
			catch (XMPPException e1) {
				Log.i(TAG,getText(R.string.error_delivering_message).toString()+e1.getMessage());
	    		runOnUiThread(new MessageRunnable(getText(R.string.error_delivering_message).toString()+e1.getMessage(),useVerbosity, Constants.VIEW_LOG));
			}
			catch (Exception e2) {
				Log.i(TAG,getText(R.string.error_delivering_message).toString()+e2.getMessage());
				runOnUiThread(new MessageRunnable(getText(R.string.error_delivering_message).toString()+e2.getMessage(),useVerbosity, Constants.VIEW_LOG));
			}
		}
	}
		
	private void sendInsertedMessage(String messageInserted) {
		if (isConnected()) {
			ChatManager chatmanager = connection.getChatManager();
			
			String receiver_user = REMOTE_MANAGER_ACCOUNT; //manager_dispositivi_thomas
			String server_name = REMOTE_SERVER_NAME; // ripley
			
			// Create listener that prints received messages
			Chat newChat = chatmanager.createChat(receiver_user+"@"+server_name, new MessageListener()	{
				public void processMessage(Chat chat, Message message) {	
					
					Log.i(TAG,message.getBody());
					runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody(),useVerbosity, Constants.VIEW_LOG)); // FIXME ASMACK BUG
					
					if (message.getBody()!=null) {
						Log.i(TAG,message.getFrom()+": "+message.getBody());
						runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody(),useVerbosity, Constants.VIEW_LOG));
					}
				}
			});
			
			try {
				if (!messageInserted.equals("")) { // user inserted some text
					newChat.sendMessage(messageInserted);
					Log.i(TAG,"'"+messageInserted+"'"+" "+getText(R.string.sent_successfully).toString());
					runOnUiThread(new MessageRunnable("'"+messageInserted+"'"+" "+getText(R.string.sent_successfully).toString(),
													  useVerbosity, Constants.VIEW_BOTH));
					showCustomDialog(getText(R.string.dialog_title).toString(),
												"'"+messageInserted+"'"+" "+getText(R.string.sent_successfully).toString(),
												R.drawable.message,
												getText(R.string.dialog_ok).toString());
				}
			}
			catch (XMPPException e1) {
				Log.i(TAG,getText(R.string.error_delivering_message).toString()+e1.getMessage());
				runOnUiThread(new MessageRunnable(getText(R.string.error_delivering_message).toString()+e1.getMessage(),useVerbosity, Constants.VIEW_LOG));
			}
			catch (Exception e2) {
				Log.i(TAG,getText(R.string.error_delivering_message).toString()+e2.getMessage());
				runOnUiThread(new MessageRunnable(getText(R.string.error_delivering_message).toString()+e2.getMessage(),useVerbosity, Constants.VIEW_LOG));
			}
		}
	}
	
	/**
	 * Method called when user click on checkbox to enable/disable server forwarding messages only to vehicles near sender.
	 */
	private void sendReceiversSetting() {
		if (isConnected()) {
			ChatManager chatmanager = connection.getChatManager();
			
			String receiver_user = REMOTE_MANAGER_ACCOUNT; //manager_dispositivi_thomas
			String server_name = REMOTE_SERVER_NAME; // ripley
			
			// Create listener that prints received messages
			Chat newChat = chatmanager.createChat(receiver_user+"@"+server_name, new MessageListener()	{
				public void processMessage(Chat chat, Message message) {	
					
					Log.i(TAG,message.getBody());
					runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody(),useVerbosity, Constants.VIEW_LOG));// FIXME ASMACK BUG
					
					if (message.getBody()!=null) {
						Log.i(TAG,message.getFrom()+": "+message.getBody());
						runOnUiThread(new MessageRunnable(message.getFrom()+": "+message.getBody(),useVerbosity, Constants.VIEW_LOG));
					}
				}
			});
			
			try {
				String message = "";

				if (!sendToAll) {
					// send chat message to server, to specify that server must forward messages only to vehicles near sender
					message = Constants.MSG_ONLY_NEAR;
				}
				else {
					// send chat message to server, to specify that server must forward messages to all vehicles
					message = Constants.MSG_TO_ALL;
				}
				
				// sending
				newChat.sendMessage(message);
				
				if (message.equals(Constants.MSG_ONLY_NEAR)) {
					Log.i(TAG,getText(R.string.messages_to_near).toString());
				}
				else {
					Log.i(TAG,getText(R.string.messages_to_all).toString());
				}
			}
			catch (XMPPException e1) {
				Log.i(TAG,getText(R.string.error_delivering_message).toString()+e1.getMessage());
				runOnUiThread(new MessageRunnable(getText(R.string.error_delivering_message).toString()+e1.getMessage(),useVerbosity, Constants.VIEW_LOG));
			}
			catch (Exception e2) {
				Log.i(TAG,getText(R.string.error_delivering_message).toString()+e2.getMessage());
				runOnUiThread(new MessageRunnable(getText(R.string.error_delivering_message).toString()+e2.getMessage(),useVerbosity, Constants.VIEW_LOG));
			}
		}
	}
	
    private void startLongRunningOperation() {
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
            	// The runnable will be run on the thread to which this handler is attached. 
            	connectionHandler.post(mUpdateGui);
                
            	if (!operationIsInterruped) { 
            		startInitOperations();
            	}
            	
            	// Causes the Runnable r to be added to the message queue. 
            	// The runnable will be run on the thread to which this handler is attached. 
            	connectionHandler.post(mUpdateResults);
            }
        };
        t.start();
    }
    
    // Create a runnable for posting
    final Runnable mUpdateGui = new Runnable() {
        public void run() {
            showDialogInUi();
        }
    };

    // Create a runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
    
    private void showDialogInUi() {
        // Back in the UI thread -- update our UI elements
    	try {
    		dialog.show();
    	}
    	catch (Exception e) {
    	}
    }	
    
    private void updateResultsInUi() {
        // Back in the UI thread -- update our UI elements based on the data in mUpdateResults
    	try {
    		dialog.dismiss();
    	}
    	catch (Exception e) {
    	}
    }
         
    @Override
    protected void onStop() {
    	super.onStop();
    	// called when Activity is no longer visible
    	// After this method I can go to:
    	// 		- onCreate (other applications need memory, process is killed) OR
    	//      - onRestart (Activity comes to foreground) 
    }

    @Override
    protected void onRestart() {
    	// called after onStop
	    super.onRestart();
	    // after onRestart will be called onStart()
    }
    
    public void onResume() {
    	super.onResume();
    	if (myLocationOverlay.enableMyLocation() == true) {
    		myLocationOverlay.runOnFirstFix(new Runnable() {
    			public void run() {
    				point = myLocationOverlay.getMyLocation();
    				mapController.animateTo(point);
    				mapController.setZoom(18);
				}
    		});
    		
    		if (myLocationOverlay.isCompassEnabled() == true) {
    			Log.i(TAG, "Compass available");
    		} 
    		else {
    			Log.i(TAG, "Compass unavailable");
    		}
    	} 
    	else {
    		Log.i(TAG, "Providers unavailable");
    	}
    }
    
    @Override
	protected void onDestroy() {
    	// before shutdown activity
		super.onDestroy();
		cleaningOperations();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

    public void onPause() {
    	super.onPause();
    	myLocationOverlay.disableCompass();
    	myLocationOverlay.disableMyLocation();
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
		
	private void showCustomDialog (String title, String message, int icon, String textButton) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(textButton, new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {
		   }
		});
		alertDialog.setIcon(icon);
		alertDialog.show();
	}
	
	private void showCustomDialogFuel (String title, String message, int icon, String textButton) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(textButton, new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) { // move on MAP TAB
			   runOnUiThread(new Runnable() {
					public void run() {
						if (!tabs.getCurrentTabTag().equals(getString(R.string.tab_map))) { // tab MAP not already selected
							tabs.setCurrentTabByTag(getString(R.string.tab_map));
						}
					}
				});
		   
		   }
		});
		alertDialog.setIcon(icon);
		alertDialog.show();
	}
	
	public void writeToNotifications(String textMsg, boolean moreVerbose) {
		if (moreVerbose) {
			textMsg = Utils.getCurrentTime()+" * "+textMsg;
		}
		
		if (textNotifications.getText().equals("")) {
			textNotifications.append(textMsg);
		} 
		else {
		    textNotifications.append("\n" + textMsg);
		    scrollerNotifications.scrollBy(0, 1000000);
		}
	}
	
	public void writeToLog(String textMsg, boolean moreVerbose) {
		if (moreVerbose) {
			textMsg = Utils.getCurrentTime()+" * "+textMsg;
		}
		
		if (textLog.getText().equals("")) {
			textLog.append(textMsg);
		} 
		else {
			textLog.append("\n" + textMsg);
		    scrollerLog.scrollBy(0, 1000000);
		}
	}
	
}
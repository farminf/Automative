package com.automotivevirtus.location;

import org.jivesoftware.smack.SmackException.NotConnectedException;

import com.automotivevirtus.xmpp.XMPPHelper;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateLocationService extends IntentService {
	
	LocationService currentLocation;
	double currentLatitude;
	double currentLongitude;
	String curLat;
	String curLong;
	
	String addressedUser2 = "android2";
	
	
	public UpdateLocationService() {
		super("UpdateLocationService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		Log.d("!!!!!", "We Are in UpdaterLocationService");
		getCurrentLocation();
		try {
			XMPPHelper.sendMessage(addressedUser2, curLat + " and " + curLong);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void getCurrentLocation() {
		// TODO Auto-generated method stub
		currentLocation = new LocationService(getApplicationContext());
		if (currentLocation.canGetLocation()) {
			currentLatitude = currentLocation.getLatitude();
			currentLongitude = currentLocation.getLongitude();

			curLat = String.valueOf(currentLatitude);
			curLong = String.valueOf(currentLongitude);

			Log.v("Location", "lat:" +curLat + " long: " + curLong);

		} else {
			// GPS or Network no available and ask user to turn on in setting
			currentLocation.showSettingsAlert();
		}
	}
}

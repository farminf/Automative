package com.automotivevirtus.location;


import com.automotivevirtus.R;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class LocationService extends Service implements LocationListener  {
	
	private final Context mContext;
	 
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    boolean canGetLocation = false;
 
    Location location; 
    double latitude; 
    double longitude; 
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
    public LocationService(Context context) {
        this.mContext = context;
        getLocation();
    }
	
	
	
	private Location getLocation() {
		// TODO Auto-generated method stub
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			// Get GPS Status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			//Get Network Status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if (!isGPSEnabled && !isNetworkEnabled){
				//No network , No GPS
				//showSettingsAlert();
			}
			else {
				this.canGetLocation = true;
				//Get location from Network
				if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
				}
				//Get location from GPS
				else {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
		}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	
		return location;
	}

	//Functions to get latitude and longitude , check if location is not empty it will return.
	//if location is empty, then it will return old latitude.
	//if we don't have any latitude or longitude, it would return 0.00
	
	public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }    
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
	
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Check if GPS or Network is unavailable
	public boolean canGetLocation() {
        return this.canGetLocation;
    }
	
	//showing Alert to user to turn on GPS and Network Setting
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		//Alert Title
		alertDialog.setTitle(R.string.gps_alert_title);
		
		//Alert Message
		alertDialog.setMessage(R.string.alert_message);
		
		// Set Icon to Alert
        //alertDialog.setIcon(R.drawable.delete);
		
		//if pressing positive button
		alertDialog.setPositiveButton(R.string.server_setting , new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
		//if pressing negative button
		 alertDialog.setNegativeButton(R.string.cancel , new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int which) {
				 dialog.cancel();
	            }
	        });
		 
		 //Show Alert 
		 alertDialog.show();
  
	}
	
	//Stop using GPS
	public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationService.this);
        }       
    }
}

package com.automotivevirtus.activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.automotivevirtus.R;
import com.automotivevirtus.db.DBAdapter;
import com.automotivevirtus.location.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SecondTab extends Fragment {

	

	SupportMapFragment fragment;

	MapView mapView;
	GoogleMap map;
	LatLng CENTER = null;
	LatLng Accident = null;
	LatLng Traffic = null;
	LatLng Weather = null;
	LatLng Traffic2 = null;

	double latitudeDouble;
	double longitudeDouble;

	double accidentLatitudeDouble;
	double accidentLongitudeDouble;

	double trafficLatitudeDouble;
	double trafficLongitudeDouble;

	double weatherLatitudeDouble;
	double weatherLongitudeDouble;

	double trafficLatV;
	double trafficLonV;
	DBAdapter myDb;

	Location location;

	String CurrentLocationTag = "Current Location";

	LocationService LS;

	SharedPreferences sharedPref;
	Boolean accidentMarker = false;
	Boolean trafficMarker = false;
	Boolean weatherMarker = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.second_tab, container, false);

		accidentMarker = getAccidentSharedPref();
		trafficMarker = getTrafficSharedPref();
		weatherMarker = getWeatherSharedPref();
		
		Log.d("test", "oncreate in map done");
		Log.d("test", "oncreate in map done");

		accidentLatitudeDouble = 45.061010;
		accidentLongitudeDouble = 7.658942;

		trafficLatitudeDouble = 45.061990;
		trafficLongitudeDouble = 7.651042;

		weatherLatitudeDouble = 45.062401;
		weatherLongitudeDouble = 7.65210;

		// Get Location , Lat and Long
		LS = new LocationService(getActivity());
		getCurrentLocation();
		CENTER = new LatLng(latitudeDouble, longitudeDouble);
		Accident = new LatLng(accidentLatitudeDouble, accidentLongitudeDouble);
		Traffic = new LatLng(trafficLatitudeDouble, trafficLongitudeDouble);
		Weather = new LatLng(weatherLatitudeDouble, weatherLongitudeDouble);

		myDb = new DBAdapter(getActivity());
		myDb.open();
		if (myDb.checkDBEmpty() != 0) {
			String rowno = Integer.toString(myDb.checkDBEmpty());
			Log.d("DB row", rowno);
			Cursor cursor = myDb.getRow(10);
			trafficLatV = cursor.getDouble(DBAdapter.COL_LAT);
			trafficLonV = cursor.getDouble(DBAdapter.COL_LON);
			Traffic2 = new LatLng(trafficLatV, trafficLonV);

		}

		

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// myDb = new DBAdapter(getActivity());
		// myDb.open();
		//

		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		accidentMarker = getAccidentSharedPref();
		trafficMarker = getTrafficSharedPref();
		weatherMarker = getWeatherSharedPref();
		Log.d("test", "resume in map done");
		Log.d("test", "resume in map done");

		if (map == null) {
			map = fragment.getMap();

			if (CENTER != null) {
				// Current Location Marker
				map.addMarker(new MarkerOptions().position(CENTER)
						.title(CurrentLocationTag).snippet(""));

			}
			// Accident Marker
			if (accidentMarker) {
				Marker accidentMarkermap = map
						.addMarker(new MarkerOptions()
								.position(Accident)
								.title("Accident")
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
								.snippet("extra info"));
				accidentMarkermap.showInfoWindow();
			}

			// Traffic Marker
			if (trafficMarker) {
				Marker trafficMarkermap = map
						.addMarker(new MarkerOptions()
								.position(Traffic)
								.title("Traffic")
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
								.snippet("extra info"));

				trafficMarkermap.showInfoWindow();

				if (Traffic2 != null) {
					// Traffic2 Marker
					Marker trafficMarkermap2 = map
							.addMarker(new MarkerOptions()
									.position(Traffic2)
									.title("Traffic2")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
									.snippet("extra info"));

					trafficMarkermap2.showInfoWindow();
				}
			}

			// Weather Marker
			if (weatherMarker) {
				Marker weatherMarkermap = map
						.addMarker(new MarkerOptions()
								.position(Weather)
								.title("Weather")
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
								.snippet("extra info"));

				weatherMarkermap.showInfoWindow();

			}
			// ------
			map.setIndoorEnabled(true);
			map.setMyLocationEnabled(true);
			map.moveCamera(CameraUpdateFactory.zoomTo(14));

			if (CENTER != null) {
				map.animateCamera(CameraUpdateFactory.newLatLng(CENTER), 1750,
						null);
			}

			// add circle
			CircleOptions circle = new CircleOptions();
			circle.center(CENTER).fillColor(Color.BLUE).radius(10);
			map.addCircle(circle);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	// -------------------------------------------------------------------

	private void getCurrentLocation() {
		// TODO Auto-generated method stub
		if (LS.canGetLocation()) {

			latitudeDouble = LS.getLatitude();
			longitudeDouble = LS.getLongitude();

			String curLat = String.valueOf(latitudeDouble);
			String curLong = String.valueOf(longitudeDouble);

			Log.v("Location in Second Tab", curLat + " " + curLong);

		} else {
			// GPS or Network no available and ask user to turn on in setting

			LS.showSettingsAlert();
		}

	}

	private boolean getAccidentSharedPref() {

		sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		accidentMarker = sharedPref.getBoolean("accidentMarker", false);
		return accidentMarker;
	}

	private boolean getTrafficSharedPref() {

		sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		trafficMarker = sharedPref.getBoolean("trafficMarker", false);
		return trafficMarker;
	}

	private boolean getWeatherSharedPref() {

		sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		weatherMarker = sharedPref.getBoolean("weatherMarker", false);
		return weatherMarker;
	}
}

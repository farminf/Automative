package com.automotivevirtus.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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
	LatLng Traffic2 = null;

	double latitudeDouble;
	double longitudeDouble;

	double accidentLatitudeDouble;
	double accidentLongitudeDouble;
	
	double trafficLatitudeDouble;
	double trafficLongitudeDouble;
	
	double trafficLatV;
	double trafficLonV;
    DBAdapter myDb;


	Location location;

	String CurrentLocationTag = "Current Location";

	LocationService LS;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.second_tab, container, false);

		accidentLatitudeDouble = 45.061010;
		accidentLongitudeDouble = 7.658942;
		
		trafficLatitudeDouble = 45.061990;
		trafficLongitudeDouble = 7.651042;

		// Get Location , Lat and Long
		LS = new LocationService(getActivity());
		getCurrentLocation();
		CENTER = new LatLng(latitudeDouble, longitudeDouble);
		Accident = new LatLng(accidentLatitudeDouble, accidentLongitudeDouble);
		Traffic = new LatLng(trafficLatitudeDouble, trafficLongitudeDouble);
		
		myDb = new DBAdapter(getActivity());
        myDb.open();
		if (myDb.checkDBEmpty() != 0){
		String rowno = Integer.toString(myDb.checkDBEmpty());
		Log.d("DB row", rowno );
		Cursor cursor = myDb.getRow(10);
		trafficLatV = cursor.getDouble(DBAdapter.COL_LAT);
		trafficLonV = cursor.getDouble(DBAdapter.COL_LON);
		Traffic2 = new LatLng(trafficLatV, trafficLonV);

		} 
		
		//
		// // Get Map and Set it
		// mapView = (MapView) view.findViewById(R.id.map);
		// mapView.onCreate(savedInstanceState);
		//
		// setMapView();

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
//		myDb = new DBAdapter(getActivity());
//        myDb.open();
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
		if (map == null) {
			map = fragment.getMap();
			
			//Current Location Marker
			map.addMarker(new MarkerOptions().position(CENTER)
					.title(CurrentLocationTag).snippet(""));
			//Accident Marker
			Marker accidentMarker = map.addMarker(new MarkerOptions()
					.position(Accident)
					.title("Accident")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
					.snippet("extra info"));
			accidentMarker.showInfoWindow();

			//Traffic Marker
			Marker trafficMarker = map.addMarker(new MarkerOptions()
			.position(Traffic)
			.title("Traffic")
			.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
			.snippet("extra info"));
			
			trafficMarker.showInfoWindow();
			
			//Traffic2 Marker
			Marker trafficMarker2 = map.addMarker(new MarkerOptions()
			.position(Traffic2)
			.title("Traffic2")
			.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
			.snippet("extra info"));
			
			trafficMarker2.showInfoWindow();
			
			
			//------
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

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------
	// **************************************************************************

	// private void setMapView() {
	// // TODO Auto-generated method stub
	//
	// MapsInitializer.initialize(getActivity());
	//
	// switch (GooglePlayServicesUtil
	// .isGooglePlayServicesAvailable(getActivity())) {
	//
	// case ConnectionResult.SUCCESS:
	//
	// Log.d("info", "connection map success!!");
	//
	// map = mapView.getMap();
	// if (map == null) {
	//
	// Log.d("", "Map Fragment Not Found or no Map in it!!");
	//
	// }
	// map.clear();
	//
	// try {
	// map.addMarker(new MarkerOptions().position(CENTER)
	// .title(CurrentLocationTag).snippet(""));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// map.setIndoorEnabled(true);
	// map.setMyLocationEnabled(true);
	// map.moveCamera(CameraUpdateFactory.zoomTo(5));
	// if (CENTER != null) {
	// map.animateCamera(CameraUpdateFactory.newLatLng(CENTER), 1750,
	// null);
	// }
	// // add circle
	// CircleOptions circle = new CircleOptions();
	// circle.center(CENTER).fillColor(Color.BLUE).radius(10);
	// map.addCircle(circle);
	// map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	//
	// break;
	//
	// case ConnectionResult.SERVICE_MISSING:
	// Log.d("info", "connection map missing!!");
	//
	// break;
	// case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
	// Log.d("info", "connection map update required!!");
	//
	// break;
	// default:
	// Log.d("info", "connection  map default!!");
	//
	// }
	// }

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
	
	
}

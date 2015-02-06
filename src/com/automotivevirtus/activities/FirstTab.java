package com.automotivevirtus.activities;

import org.jivesoftware.smack.SmackException.NotConnectedException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.automotivevirtus.R;
import com.automotivevirtus.location.LocationService;
import com.automotivevirtus.xmpp.XMPP;
import com.automotivevirtus.xmpp.XMPPHelper;
import com.automotivevirtus.xmpp.XMPPService;

public class FirstTab extends Fragment {

	Button btnSendAccident;
	Button btnSendTraffic;
	Button btnSendWeather;
	Button btnSendMSG;

	static XMPP openFireConnection;
	SharedPreferences sharedPref;
	String username;
	String password;
	String serveraddress;
	int serverport;
	String domain;

	public ProgressDialog progressDialog;

	LocationService currentLocation;
	double currentLatitude;
	double currentLongitude;
	String curLat;
	String curLong;

	String msgAccident;
	String msgTraffic;
	String msgWeather;
	String msgCustom;

	String AddressedUser = "android2";

	AlertDialog.Builder customMSGDialog;
	
	//XMPPService xmppService = new XMPPService();

	// ------------------------------------------------------------------
	// -------------------------------On Destroy-------------------------

	// ------------------------------------------------------------------
	// -------------------------------On resume-------------------------

	// ------------------------------------------------------------------
	// -------------------------------On Create View-------------------------

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.first_tab, container, false);

		domain = getString(R.string.domain_name);

		getSharedPreference();

		// Accident Button
		btnSendAccident = (Button) view.findViewById(R.id.btnSendAccident);
		btnSendAccident.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					XMPPHelper.sendMessage(AddressedUser, msgAccident);
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Send msg", "cannot Send A msg: " + e.getMessage());

				}
			}
		});

		// Traffic Button
		btnSendTraffic = (Button) view.findViewById(R.id.btnSendTraffic);
		btnSendTraffic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					XMPPHelper.sendMessage(AddressedUser, msgTraffic);
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Send msg", "cannot Send A msg: " + e.getMessage());

				}
			}
		});

		// Weather Button
		btnSendWeather = (Button) view.findViewById(R.id.btnSendWeather);
		btnSendWeather.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					XMPPHelper.sendMessage(AddressedUser, msgWeather);
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Send msg", "cannot Send A msg: " + e.getMessage());

				}
			}
		});

		// MSG Button
		btnSendMSG = (Button) view.findViewById(R.id.btnSendMSG);
		btnSendMSG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createCustomMSGDialog();
				customMSGDialog.show();
			}
		});

		currentLocation = new LocationService(getActivity());
		getCurrentLocation();
		makeMSGs();

		return view;
	}

	// **************************************************************
	// *************************************************************
	private void getSharedPreference() {
		// TODO Auto-generated method stub
		sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		username = sharedPref.getString("textUsername", "");
		password = sharedPref.getString("textPassword", "");
		serveraddress = sharedPref.getString("textServerAddress", "");
		serverport = 5222;

	}

	private void getCurrentLocation() {
		// TODO Auto-generated method stub
		if (currentLocation.canGetLocation()) {
			currentLatitude = currentLocation.getLatitude();
			currentLongitude = currentLocation.getLongitude();

			curLat = String.valueOf(currentLatitude);
			curLong = String.valueOf(currentLongitude);

			Log.v("Location", curLat + " " + curLong);

		} else {
			// GPS or Network no available and ask user to turn on in setting
			currentLocation.showSettingsAlert();
		}

	}

	private void makeMSGs() {
		// TODO Auto-generated method stub
		msgAccident = "accident " + curLat + " " + curLong;
		msgTraffic = "traffic " + curLat + " " + curLong;
		msgWeather = "weather " + curLat + " " + curLong;

	}

	private void createCustomMSGDialog() {
		customMSGDialog = new AlertDialog.Builder(getActivity());
		// Edit text to get user message
		final EditText inputMSG = new EditText(getActivity());
		customMSGDialog.setView(inputMSG);

		customMSGDialog
				.setMessage(R.string.custom_message_dialog)
				.setPositiveButton(R.string.send,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Read user input
								Editable Input = inputMSG.getText();
								String customMSGInput = Input.toString();

								try {
									XMPPHelper.sendMessage(AddressedUser,
											customMSGInput);
								} catch (NotConnectedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Log.d("Send msg",
											"cannot Send A msg: "
													+ e.getMessage());

								}

							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog

							}
						});
		// Create the AlertDialog
		customMSGDialog.create();

	}
}

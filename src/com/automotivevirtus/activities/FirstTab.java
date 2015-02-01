package com.automotivevirtus.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.automotivevirtus.R;
import com.automotivevirtus.xmpp.XMPP;
import com.automotivevirtus.xmpp.XMPPService;

public class FirstTab extends Fragment {

	Button btnStartService;
	static XMPP openFireConnection;
	SharedPreferences sharedPref;
	String username;
	String password;
	String serveraddress;
	int serverport;
	String domain;

	public ProgressDialog progressDialog;

	// ------------------------------------------------------------------
	// -------------------------------On Destroy-------------------------

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();

	}

	// ------------------------------------------------------------------
	// -------------------------------On resume-------------------------

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	// ------------------------------------------------------------------
	// -------------------------------On Create View-------------------------

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.first_tab, container, false);

		domain = getString(R.string.domain_name);

		getSharedPreference();

		// Alert Dialogue for XMPP lost
		// -------------------------------
		// final AlertDialog.Builder errorDialog = new AlertDialog.Builder(
		// getActivity());
		// errorDialog
		// .setMessage(R.string.no_XMPP_message)
		// .setPositiveButton(R.string.server_setting,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// // User clicked setting button
		// // Mobile setting will show up
		// startActivityForResult(
		// new Intent(
		// android.provider.Settings.ACTION_SETTINGS),
		// 0);
		// }
		// })
		// .setNegativeButton(R.string.cancel,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// // User cancelled the dialog
		//
		// }
		// });
		// // Create the AlertDialog
		// errorDialog.create();

		btnStartService = (Button) view.findViewById(R.id.btnStartService);
		btnStartService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// showProgressBar();
				Intent serviceIntent = new Intent(getActivity()
						.getBaseContext(), XMPPService.class);
				getActivity().startService(serviceIntent);
			}
		});

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

	public void showProgressBar() {

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("Loading..");
		progressDialog.setTitle("Checking Network");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		progressDialog.show();

	}

	public void dismissProgressBar() {
		progressDialog.dismiss();
	}

}

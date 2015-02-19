package com.automotivevirtus.activities;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.automotivevirtus.R;
import com.automotivevirtus.xmpp.XMPPHelper;

public class ThirdTab extends Fragment {

	String[] virtusServices;
	// CustomAdapter adapter;
	ArrayAdapter<String> adapter;
	ListView listview;
	SharedPreferences sharedPref;
	String accidentPubSub;
	String trafficPubSub;
	String weatherPubSub;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.third_tab, container, false);
		// Read pub sub nodes name from Sharedpreference
		getSharedPreference();

		listview = (ListView) view.findViewById(R.id.lvServices);

		// Set the values in list adapter
		virtusServices = new String[] { "Accident", "Traffic", "Weather"

		};

		// adapter = new CustomAdapter(getActivity(), services);
		adapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_list_item_multiple_choice,
				virtusServices);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ListView lv = (ListView) parent;
				if (lv.isItemChecked(position)) {

					Toast.makeText(getActivity(),
							"You checked " + virtusServices[position],
							Toast.LENGTH_SHORT).show();
					// Subscribe to checked node
					switch (virtusServices[position]) {
					case "Accident":
						try {
							XMPPHelper.subscribeToNode(accidentPubSub);
						} catch (NoResponseException | XMPPErrorException
								| NotConnectedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("error", "cannot subscribe to node"
									+ virtusServices[position]);
						}
						break;

					case "Traffic":
						try {
							XMPPHelper.subscribeToNode(trafficPubSub);
						} catch (NoResponseException | XMPPErrorException
								| NotConnectedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("error", "cannot subscribe to node"
									+ virtusServices[position]);
						}
						break;

					case "Weather":
						try {
							XMPPHelper.subscribeToNode(weatherPubSub);
						} catch (NoResponseException | XMPPErrorException
								| NotConnectedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("error", "cannot subscribe to node"
									+ virtusServices[position]);
						}
						break;

					default:
						break;
					}

				} else {
					// unsubscribe from the node that user has unchecked
					switch (virtusServices[position]) {
					case "Accident":
						try {
							XMPPHelper.unSubscribeToNode(accidentPubSub);
						} catch (NoResponseException | XMPPErrorException
								| NotConnectedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("error", "cannot unsubscribe to node"
									+ virtusServices[position]);
						}
						break;

					case "Traffic":
						try {
							XMPPHelper.unSubscribeToNode(trafficPubSub);
						} catch (NoResponseException | XMPPErrorException
								| NotConnectedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("error", "cannot unsubscribe to node"
									+ virtusServices[position]);
						}
						break;

					case "Weather":
						try {
							XMPPHelper.unSubscribeToNode(weatherPubSub);
						} catch (NoResponseException | XMPPErrorException
								| NotConnectedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("error", "cannot unsubscribe to node"
									+ virtusServices[position]);
						}
						break;
					default:
						break;
					}

					Toast.makeText(getActivity(),
							"You unchecked " + virtusServices[position],
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		return view;
	}

	private void getSharedPreference() {
		// TODO Auto-generated method stub
		sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		accidentPubSub = sharedPref.getString("accidentpubsubnode", "");
		trafficPubSub = sharedPref.getString("trafficpubsubnode", "");
		weatherPubSub = sharedPref.getString("weatherpubsubnode", "");

	}

}

package com.xmpp_android.settings;


import com.xmpp_android.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class Connection_Setting extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ConnectionFragment())
                .commit();
    }

	public static class ConnectionFragment extends PreferenceFragment {

		@Override
    	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.connection_setting);
    	}
	}

}
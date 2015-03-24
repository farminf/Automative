
/* PreferencesSettings.java */
/* 21/05/12 */

package it.ismb.automotive;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesSettingsActivity extends PreferenceActivity {
	   	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	  	addPreferencesFromResource(R.xml.preferences_settings);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();		
		setResult(RESULT_CANCELED, null);
	}
}

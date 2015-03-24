
/* PreferencesConnectionActivity.java */
/* 21/05/12 */

package it.ismb.automotive;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesConnectionActivity extends PreferenceActivity {
	   	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Called when the activity is first created. This is where you should do all of your normal static 
		// set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing 
		// the activity's previously frozen state, if there was one.
		// Always followed by onStart().
		super.onCreate(savedInstanceState);
	  	addPreferencesFromResource(R.xml.preferences_connection);
	}
	
	@Override
	protected void onResume() {
		// onResume is is always called after onStart, even if the app hasn't been paused
		// Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start 
		// interacting with the user. This is a good place to begin animations, open exclusive-access devices (such 
		// as the camera), etc.
		// Keep in mind that onResume is not the best indicator that your activity is visible to the user; a system 
		// window such as the keyguard may be in front. Use onWindowFocusChanged(boolean) to know for certain that 
		// your activity is visible to the user (for example, to resume a game). 
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// Called when you are no longer visible to the user. You will next receive either onRestart(), onDestroy(), 
		// or nothing, depending on later user activity.
		// Note that this method may never be called, in low memory situations where the system does not have enough 
		// memory to keep your activity's process running after its onPause() method is called. 
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// Perform any final cleanup before an activity is destroyed. This can happen either because the activity is 
		// finishing (someone called finish() on it, or because the system is temporarily destroying this instance of 
		// the activity to save space. You can distinguish between these two scenarios with the isFinishing() method.
		// Note: do not count on this method being called as a place for saving data! For example, if an activity is 
		//       editing data in a content provider, those edits should be committed in either onPause() or 
		//       onSaveInstanceState(Bundle), not here. This method is usually implemented to free resources like 
		//       threads that are associated with an activity, so that a destroyed activity does not leave such things 
		//       around while the rest of its application is still running. There are situations where the system will 
		//       simply kill the activity's hosting process without calling this method (or any others) in it, so it 
		//       should not be used to do things that are intended to remain around after the process goes away. 
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) 
		// been killed. The counterpart to onResume().
		// When activity B is launched in front of activity A, this callback will be invoked on A. B will not be created 
		// until A's onPause() returns, so be sure to not do anything lengthy here.
		// This callback is mostly used for saving any persistent state the activity is editing, to present a "edit in place" model 
		// to the user and making sure nothing is lost if there are not enough resources to start the new activity without 
		// first killing this one. This is also a good place to do things like stop animations and other things that consume 
		// a noticeable mount of CPU in order to make the switch to the next activity as fast as possible, or to close resources 
		// that are exclusive access such as the camera.
		// In situations where the system needs more memory it may kill paused processes to reclaim resources. Because of this, 
		// you should be sure that all of your state is saved by the time you return from this function. 
		// In general onSaveInstanceState(Bundle) is used to save per-instance state in the activity and this method is used 
		// to store global persistent data (in content providers, files, etc.)
		// After receiving this call you will usually receive a following call to onStop() (after the next activity has been 
		// resumed and displayed), however in some cases there will be a direct call back to onResume() without going through 
		// the stopped state. 
		
		// User pressed BACK key
		setResult(RESULT_CANCELED, null);
	}
}

package com.automotivevirtus.activities;

import android.app.Application;
import android.content.Context;

public class AutoVir extends Application {
	
	private static Context context;
	
	public void onCreate(){
	super.onCreate();
	AutoVir.context = getApplicationContext();
	}
	
	public static Context getAppContext(){
		return AutoVir.context;
	}
}

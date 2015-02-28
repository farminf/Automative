package com.automotivevirtus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	public static final String KEY_ROWID = "_id";
	public static String EVENT_NAME = "event";
	public static String LAT_NAME = "latitude";
	public static String LON_NAME = "longitude";
	
	
	public static String DATABASE_NAME = "myDB";
	public static String DATABASE_TABLE = "mainTable";
	public static int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_SQL = 
			"CREATE TABLE " + DATABASE_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ EVENT_NAME + " string not null, "
			+ LAT_NAME + " string not null, "
			+ LON_NAME + " string not null"
			+ ");";
	
	DatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(DATABASE_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w("SQLite", "Upgrading application's database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data!");
		
		// Destroy old database:
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		
		// Recreate new database:
		onCreate(db);
	}

}

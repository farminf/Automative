package com.automotivevirtus.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

	public static final String KEY_ROWID = "_id";
	public static String EVENT_NAME = "event";
	public static String LAT_NAME = "latitude";
	public static String LON_NAME = "longitude";

	public static int COL_EVENT = 1;
	public static int COL_LAT = 2;
	public static int COL_LON = 3;

	public String[] ALL_KEYS = new String[] { KEY_ROWID, EVENT_NAME, LAT_NAME,
			LON_NAME };

	public String DATABASE_NAME = "myDB";
	public static String DATABASE_TABLE = "mainTable";
	public int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE_SQL = "create table "
			+ DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + EVENT_NAME
			+ " string not null, " + LAT_NAME + " string not null, " + LON_NAME
			+ " string not null" + ");";

	private final Context context;
	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		// TODO Auto-generated constructor stub

		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);

	}

	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

	public long insertRow(String eventname, String lat, String lon) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(EVENT_NAME, eventname);
		initialValues.put(LAT_NAME, lat);
		initialValues.put(LON_NAME, lon);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public int checkDBEmpty() {

		int count = 0;
		String statement = "SELECT * FROM " + DATABASE_TABLE;
		Cursor cursor = db.rawQuery(statement, null);
		count = cursor.getCount();
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return count;
	}

	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(DATABASE_TABLE, where, null) != 0;
	}

	// Delete with name of EVENT
	public boolean deleteRowByName(String name) {
		String where = EVENT_NAME + " = " + '"' + name + '"';
		return db.delete(DATABASE_TABLE, where, null) != 0;
	}
	
	
	public void deleteAll() {
		Cursor c = getAllRows();
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(c.getLong((int) rowId));				
			} while (c.moveToNext());
		}
		c.close();
	}
	// Return all data in the database.
		public Cursor getAllRows() {
			String where = null;
			Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
								where, null, null, null, null, null);
			if (c != null) {
				c.moveToFirst();
			}
			return c;
		}
		
		// Get a specific row (by rowId)
		public Cursor getRow(long rowId) {
			String where = KEY_ROWID + "=" + rowId;
			Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
							where, null, null, null, null, null);
			if (c != null) {
				c.moveToFirst();
			}
			return c;
		}
}

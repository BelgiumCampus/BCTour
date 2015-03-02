package com.metaio.Example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "ActivityManager";
	private static final String TABLE_NAME = "Activity";
	
	
	private static final String KEY_ID ="Id"; 
	private static final String KEY_ACTIVITYNAME ="Name"; 
	private static final String KEY_LOCATION ="Location";
	
	public Databasehandler(Context context)
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		  String CREATE_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_NAME + "("
	                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ACTIVITYNAME + " TEXT,"
	                + KEY_LOCATION + " TEXT" + ")";
	        db.execSQL(CREATE_ACTIVITY_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME ); 
		 onCreate(db);
	}
	
	public Activity getActivity(String location)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor =db.query(TABLE_NAME, new String[] { KEY_ID,
	            KEY_ACTIVITYNAME, KEY_LOCATION,},KEY_LOCATION + "=?",
	            new String[]{String.valueOf(location)}, null, null, null, null);
		
		if(cursor !=null)
			cursor.moveToFirst();
			
		int activityID = Integer.parseInt(cursor.getString(0));
		String activityName = cursor.getString(1);
		String locationName = cursor.getString(2);
		
		Activity activity =  new Activity(activityID,locationName,activityName);
	
		return activity;
	}
	
	void addContact(Activity activity) {
		
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVITYNAME, activity.getActivity()); // Contact Name
        values.put(KEY_LOCATION, activity.getName()); // Contact Phone
 
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
}
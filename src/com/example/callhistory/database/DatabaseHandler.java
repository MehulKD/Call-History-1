package com.example.callhistory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
 
    // Database Name
    private static final String DATABASE_NAME = "contactsManager";
 
    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_HEADING = "heading";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_ADD_IMAGE_STATUS = "addImageStatus";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    
    /**
     * Creating Table 
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
        		//+ KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT," 
        		+ KEY_HEADING + " TEXT," 
                + KEY_COMMENT + " TEXT," 
        		+ KEY_ADD_IMAGE_STATUS + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.v("DataBaseHelper Class", "Table Created");
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * This method is used to get the comment for the ginen number
     * @param callnumber
     * @return comment
     */
	public String getComment(String callnumber) {
		String comment = null;
		try{
		SQLiteDatabase db = this.getReadableDatabase();
		
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
	            + KEY_PH_NO + " = " + callnumber; 
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor!=null && cursor.getCount()>0)
		{
		      cursor.moveToFirst();
		        do {
		        	comment = cursor.getString(3);
		        } while (cursor.moveToNext());
		      }           
		
		}catch(Exception e){
			System.out.println("Error::::" + e.getMessage());	
		}
		
		return comment;
		
		
	}
	/**
	 * This method is used to return the heading for the given number
	 * @param callnumber
	 * @return heading
	 */
	public String getHeading(String callnumber) {
		// TODO Auto-generated method stub
		String heading = null;
		try{
			
		SQLiteDatabase db = this.getReadableDatabase();
		
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
	            + KEY_PH_NO + " = " + callnumber; 
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor!=null && cursor.getCount()>0)
		{
		      cursor.moveToFirst();
		        do {
		        	heading = cursor.getString(2);
		        } while (cursor.moveToNext());
		      }  
		
		}catch(Exception e){
			System.out.println("Error::::" + e.getMessage());	
		}
		
		return heading;
	}
	
	/**
	 * This method is used to update the comment and heading data stored for the given number
	 * @param _phoneNumber
	 * @param heading
	 * @param comment
	 */
	public void updateComment(String _phoneNumber, String heading,
			String comment) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
        args.put(KEY_HEADING, heading);
        args.put(KEY_COMMENT, comment);
        db.update(TABLE_CONTACTS, args,KEY_PH_NO + "= ?", new String[]{ _phoneNumber });
        args.clear();
		
	}
	
	/**
	 * This method is used to store the new comment, heading and phone number in contacts table
	 * @param _phoneNumber
	 * @param heading
	 * @param comment
	 */
	public void addContact(String _phoneNumber, String heading, String comment) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(KEY_PH_NO, _phoneNumber);
		args.put(KEY_HEADING, heading);
	    args.put(KEY_COMMENT, comment);
	 // Inserting Row
        db.insert(TABLE_CONTACTS, null, args);
        db.close(); // Closing database connection
	}
	
	/**
	 * This method is used to delete the record from the contacts table bye the given number
	 * @param selectedNum
	 */
	public void deleteComment(String selectedNum) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + KEY_PH_NO + " = '"+ selectedNum + "'");
	}

 
}

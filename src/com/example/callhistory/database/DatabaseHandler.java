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
 
    // Creating Tables
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
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
//    // Adding new contact
//    public void addContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
// 
//        ContentValues values = new ContentValues();
//        //values.put(KEY_NAME, contact.getName()); // Contact Name
//        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone
//        values.put(KEY_HEADING, contact.getHeading());// Contact Heading
//        values.put(KEY_COMMENT, contact.getComment()); // Contact Comment
// 
//        // Inserting Row
//        db.insert(TABLE_CONTACTS, null, values);
//        db.close(); // Closing database connection
//    }
// 
//    // Getting single contact
//    Contact getContact(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
// 
//        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
//                KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
// 
//        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2));
//        // return contact
//        return contact;
//    }
//     
//    // Getting All Contacts
//    public List<Contact> getAllContacts() {
//        List<Contact> contactList = new ArrayList<Contact>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
// 
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
// 
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                Contact contact = new Contact();
//                contact.setID(Integer.parseInt(cursor.getString(0)));
//                contact.setPhoneNumber(cursor.getString(1));
//                contact.setHeading(cursor.getString(2));
//                contact.setComment(cursor.getString(3));
//                // Adding contact to list
//                contactList.add(contact);
//            } while (cursor.moveToNext());
//        }
// 
//        // return contact list
//        return contactList;
//    }
// 
//    // Updating single contact
//    public int updateContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
// 
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
// 
//        // updating row
//        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//    }
// 
//    // Deleting single contact
//    public void deleteContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//        db.close();
//    }
// 
// 
//    // Getting contacts Count
//    public int getContactsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
// 
//        // return count
//        return cursor.getCount();
//    }

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

 
}

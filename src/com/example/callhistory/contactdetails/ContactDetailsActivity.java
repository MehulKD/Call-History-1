package com.example.callhistory.contactdetails;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.callhistory.R;

public class ContactDetailsActivity extends ActionBarActivity{
	
	final static String LOG_TAG = "Contact Details Activity";
	private String contactName, contactNumber;
	private TextView contactName_tv, contactnumber_tv, viewContact_tv;
	private RelativeLayout callContact_rl, sendMessage_rl, viewContact_rl, deleteFromLog_rl, addComment_rl, scheduletask_rl;
	private ImageView viewContact_iv;
	private String contactId = null;
	Context context = null;
	private long rawContactId = -1;
	private String firstContactId = null;
	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    int rawContactInsertIndex = ops.size();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_contact_details);
	 context=this;
	
	 InitializeUI();
	 
	 contactName = getIntent().getExtras().getString("key_contact_name");
	 contactNumber = getIntent().getStringExtra("key_contact_number");
	 
	 contactId = getContactRowIDLookupList(contactNumber, context);
	 
	 StringTokenizer tokens = new StringTokenizer(contactId, ",");
	 firstContactId = tokens.nextToken();// this will contain "Fruit"
	
	 rawContactId = getRawContactId(firstContactId);
	 
	 
	 /**
	  * this method is used to set contact name and number to the corresponding textviews
	  * and pass the parameters contactName and contactNumber
	  */
	 setContactNameNumber(contactName, contactNumber);
	 
	 callContact_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent callIntent = new Intent(Intent.ACTION_CALL);
		    callIntent.setData(Uri.parse("tel:" + contactNumber));
		    startActivity(callIntent);
		}
	});
	 
	 sendMessage_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
			smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
			smsIntent.setType("vnd.android-dir/mms-sms");
			smsIntent.setData(Uri.parse("sms:" + contactNumber)); 
			startActivity(smsIntent);
		}
	});
	 
	 viewContact_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(contactName == null){
				
				Intent i = new Intent();     
				i.setAction(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT);
				i.setData(Uri.fromParts("tel", contactNumber, null)); 
				startActivity(i);
			}else{
				StringTokenizer tokens = new StringTokenizer(contactId, ",");
				String firstContactId = tokens.nextToken();// this will contain "Fruit"
				//String second = tokens.nextToken();// this will contain " they taste good"
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(firstContactId));
				intent.setData(uri);
				context.startActivity(intent);
			}
			
		}
	});
	 
	 deleteFromLog_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			deleteNumberFromLog(contactNumber);
		}

		/**
		 * this method is used to delete the selected number from call log
		 * @param contactNumber
		 */
		private void deleteNumberFromLog(String contactNumber) {
			// TODO Auto-generated method stub
			try { 
			    
				Uri CALLLOG_URI = Uri.parse("content://call_log/calls"); 
			    getApplicationContext().getContentResolver().delete(CALLLOG_URI,CallLog.Calls.NUMBER +"=?",new String[]{contactNumber});
			    ContactDetailsActivity.this.finish();
			
			}catch(Exception e){ 
				
			    e.printStackTrace(); 
			} 
			
		}
	});
	 
	 addComment_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			addCommentDialog(rawContactId);
			
		}
	});
	 
	}

	/**
	 * This method used to get the raw contact id from the contact by using contact id
	 * @param contactId2
	 * @return rawContactId
	 */
	private long getRawContactId(String contactId2) {
		// TODO Auto-generated method stub
		long rawContact = -1;
		String[] projection=new String[]{ContactsContract.RawContacts._ID};
	    String selection=ContactsContract.RawContacts.CONTACT_ID+"=?";
	    String[] selectionArgs=new String[]{String.valueOf(contactId2)};
	    
	    Cursor c=context.getContentResolver()
	    		.query(ContactsContract.RawContacts.CONTENT_URI,projection,selection,selectionArgs , null);
//	    Cursor c = getContentResolver().query(RawContacts.CONTENT_URI,
//	    	    new String[]{RawContacts._ID},
//	    	    RawContacts.CONTACT_ID + "=?",
//	    	    new String[]{String.valueOf(firstContactId)}, null);
//	    try {
//	        if (c.moveToFirst()) {
//	        	rawContact = c.getLong(0);
//	        }
//	    } finally {
//	        c.close();
//	    }
	    if (c.moveToFirst()) {    
	        rawContact=c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
	    }
		 Toast.makeText(getApplicationContext(), "Raw Contact ID: "+rawContact, Toast.LENGTH_LONG).show();
	  		return rawContact; 
		
	}

	/**
	  * Gets a list of contact ids that is pointed at the passed contact number
	  * parameter
	  * 
	  * @param contactNo
	  *            contact number whose contact Id is requested (no special chars)
	  * @return String representation of a list of contact ids pointing to the
	  *         contact in this format 'ID1','ID2','34','65','12','17'...
	  */
	 public static String getContactRowIDLookupList(String contactNo, Context cxt) {

	     String contactNumber = Uri.encode(contactNo);
	     String contactIdList = new String();
	     if (contactNumber != null) {
	         Cursor contactLookupCursor = cxt.getContentResolver()
	        		 .query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, 
	        		 Uri.encode(contactNumber)),
	                 new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
	                 null, null, null);
	         if (contactLookupCursor != null) {
	             while (contactLookupCursor.moveToNext()) {
	                 int phoneContactID = contactLookupCursor
	                         .getInt(contactLookupCursor
	                                 .getColumnIndexOrThrow(PhoneLookup._ID));
	                 if (phoneContactID > 0) {
	                     contactIdList += "" + phoneContactID + ",";
	                 }
	             }
	             if (contactIdList.endsWith(",")) {
	                 contactIdList = contactIdList.substring(0,
	                         contactIdList.length() - 1);
	             }
	         }
	         contactLookupCursor.close();
	     }
	     return contactIdList;
	 }

	/**
	 * This method is used to set contact name and number to the corresponding textviews 
	 * parameters are getting from previous activity by getIntent()
	 * @param contactName2
	 * @param contactNumber2
	 */
	private void setContactNameNumber(String contactName2,
			String contactNumber2) {
		// TODO Auto-generated method stub
		if(null != contactName2){
			contactName_tv.setText("Call " +contactName2);
			viewContact_iv.setBackgroundResource(R.drawable.ic_action_person);
		}else{
			contactName_tv.setText("Call back");
			viewContact_tv.setText("Add to contacts");
			viewContact_iv.setBackgroundResource(R.drawable.ic_action_add_person);
		}
		
		contactnumber_tv.setText("Number: " +contactNumber2);
	}
	
	 /**
	  * this method is used to display the dialog when user clicks on add comment rl and save the given note 
	  * to the contact note field
	  * @param rawContactId2
	  */
	private void addCommentDialog(final long rawContactId2) {
		// TODO Auto-generated method stub
		LayoutInflater inflator = LayoutInflater.from(context);
		View commentView = inflator.inflate(R.layout.comment_layout, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setTitle("Add Comment");
		alertDialogBuilder.setCancelable(false);
		//alert.setMessage("Message");
		
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(commentView);

		// Set an EditText view to get user input 
		final EditText heading_edt = (EditText) commentView.findViewById(R.id.heading_editText);
		final EditText comment_edt = (EditText) commentView.findViewById(R.id.comment_editText);
	

		alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		
			public void onClick(DialogInterface dialog, int whichButton) {
				
			  String heading = heading_edt.getText().toString();
			  String comment = comment_edt.getText().toString();
			  // Do something with values!
			 // Inserting Contacts
		      
		      if(heading.length() > 5){
		    	  
		    	  if(heading.length() < 16){
		    		  
		    		  String note = heading+","+comment;
		    		  
		    		  ContentValues contentValues =  new ContentValues();
		  		      // insert name data
		  		      contentValues.clear();
		              contentValues.put(ContactsContract.Data.CONTACT_ID, firstContactId);
		              contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
		              contentValues.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
		              getContentResolver().insert(ContactsContract.Contacts.CONTENT_URI, contentValues);
		              //Log.d("TC", "Update returned : " + stat);
		    		  
//				      if(null == dbHandler.getHeading(_phoneNumber)){
//				    	  
//				    	  Log.d("Insert: ", "Inserting .."); 
//
//				    	  dbHandler.addContact(_phoneNumber, heading, comment); 
//				    	  notifyDataSetChanged();
//				      }
		    	  }else{
		    		  Toast.makeText(getApplicationContext(), "Heading shouldn't exceed 15 charcaters!", Toast.LENGTH_LONG).show();
		    	  }
		      }else{
		    	  Toast.makeText(getApplicationContext(), "Heading must contain minimum of 6 characters!", Toast.LENGTH_LONG).show();
		      }
			  
			  }
			});

			alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				  dialog.cancel();
			  }
			});
			
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.show();
		}

	private void InitializeUI() {
		// TODO Auto-generated method stub
		contactName_tv = (TextView) findViewById(R.id.call_contact_name_tv);
		contactnumber_tv = (TextView) findViewById(R.id.call_contact_num_tv);
		viewContact_tv = (TextView) findViewById(R.id.view_contact_tv);
		viewContact_iv = (ImageView) findViewById(R.id.view_contact_imageView);
		callContact_rl = (RelativeLayout) findViewById(R.id.contact_nameNum_relativeLayout);
		sendMessage_rl = (RelativeLayout) findViewById(R.id.send_message_relativeLayout);
		viewContact_rl = (RelativeLayout) findViewById(R.id.view_contact_relativeLayout);
		deleteFromLog_rl = (RelativeLayout) findViewById(R.id.del_from_log_relativeLayout);
		addComment_rl = (RelativeLayout) findViewById(R.id.add_comment_relativeLayout);
		scheduletask_rl = (RelativeLayout) findViewById(R.id.schedule_task_relativeLayout);
	}

}

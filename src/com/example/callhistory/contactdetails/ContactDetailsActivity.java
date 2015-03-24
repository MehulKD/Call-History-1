package com.example.callhistory.contactdetails;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.callhistory.MainActivity;
import com.example.callhistory.R;

public class ContactDetailsActivity extends Activity{
	
	final static String LOG_TAG = "Contact Details Activity";
	private String contactName, contactNumber;
	private TextView contactName_tv, contactnumber_tv, viewContact_tv;
	private RelativeLayout callContact_rl, sendMessage_rl, viewContact_rl, clearAllCalls_rl, addComment_rl, scheduletask_rl,
		editNumber_rl, share_rl, copy_rl, clearThisCall_rl;
	private ImageView viewContact_iv;
	private String contactId = null;
	Context context = null;
	private long rawContactId = -1;
	private String firstContactId = null;
	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    int rawContactInsertIndex = ops.size();
    String call_contact_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_contact_details);
	 context=this;
	
	 InitializeUI();
	 
	 contactName = getIntent().getExtras().getString("key_contact_name");
	 contactNumber = getIntent().getStringExtra("key_contact_number");
	 call_contact_id = getIntent().getStringExtra("key_contact_call_id");
	 
	 
	 if(null !=contactName){
		 contactId = getContactRowIDLookupList(contactNumber, context);
		 StringTokenizer tokens = new StringTokenizer(contactId, ",");
		 firstContactId = tokens.nextToken();// this will contain "Fruit"
		 rawContactId = getRawContactId(firstContactId);
	 }
	 
	 
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
		    finish();
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
			finish();
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
				String firstContactId = tokens.nextToken();
				//String second = tokens.nextToken();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(firstContactId));
				intent.setData(uri);
				context.startActivity(intent);
			}
			
		}
	});
	 
	 editNumber_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent editNumIntent = new Intent(ContactDetailsActivity.this, MainActivity.class).putExtra("edit_contact", contactNumber);
			startActivity(editNumIntent);
			finish();
		}
	});
	 
	 share_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/html");
			//sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text that will be shared.</p>"));
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, contactName +"\n" +contactNumber );
			startActivity(Intent.createChooser(sharingIntent,"Share contact"));
			finish();
		}
	});
	 
	 copy_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("copied", contactName +"\n" +contactNumber);
			clipboard.setPrimaryClip(clip);
			Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_LONG).show();
			finish();
		}
	});
	 
	 clearThisCall_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				context.getContentResolver().delete( CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " = ? ", new String[] { String.valueOf(call_contact_id) });
	            // int call_contact_id = 0; // I assume you have this id;
	            //getContentResolver().delete(Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, String.valueOf(call_contact_id)), "", null);
	            finish();
	           }catch (Exception ex) {
	              System.out.print("Exception here ");
	           }
		}
	});
	 
	 clearAllCalls_rl.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			deleteAllEntriesFromLog(contactNumber);
		}

		/**
		 * this method is used to delete the selected number from call log
		 * @param contactNumber
		 */
		private void deleteAllEntriesFromLog(String contactNumber) {
			// TODO Auto-generated method stub
			try { 
			    
				Uri CALLLOG_URI = Uri.parse("content://call_log/calls"); 
			    getApplicationContext().getContentResolver().delete(CALLLOG_URI,CallLog.Calls.NUMBER +"=?",new String[]{contactNumber});
			    ContactDetailsActivity.this.finish();
			    finish();
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

	    if (c.moveToFirst()) {    
	        rawContact=c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
	    }
		 //Toast.makeText(getApplicationContext(), "Raw Contact ID: "+rawContact, Toast.LENGTH_LONG).show();
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
			//viewContact_iv.setBackgroundResource(R.drawable.ic_action_person);
		}else{
			contactName_tv.setText("Call back");
			viewContact_tv.setText("Add to contacts");
			//viewContact_iv.setBackgroundResource(R.drawable.ic_action_add_person);
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
		  		      // insert note data
		  		      contentValues.clear();
		              contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
		              contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
		              contentValues.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
		              getContentResolver().insert(ContactsContract.Contacts.CONTENT_URI, contentValues);

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
		//viewContact_iv = (ImageView) findViewById(R.id.view_contact_imageView);
		callContact_rl = (RelativeLayout) findViewById(R.id.contact_nameNum_relativeLayout);
		sendMessage_rl = (RelativeLayout) findViewById(R.id.send_message_relativeLayout);
		viewContact_rl = (RelativeLayout) findViewById(R.id.view_contact_relativeLayout);
		clearAllCalls_rl = (RelativeLayout) findViewById(R.id.del_all_from_log_relativeLayout);
		addComment_rl = (RelativeLayout) findViewById(R.id.add_comment_relativeLayout);
		scheduletask_rl = (RelativeLayout) findViewById(R.id.schedule_task_relativeLayout);
		scheduletask_rl = (RelativeLayout) findViewById(R.id.schedule_task_relativeLayout);
		editNumber_rl = (RelativeLayout) findViewById(R.id.edit_contact_relativeLayout);
		share_rl = (RelativeLayout) findViewById(R.id.share_contact_relativeLayout); 
		copy_rl = (RelativeLayout) findViewById(R.id.copy_contact_relativeLayout); 
		clearThisCall_rl = (RelativeLayout) findViewById(R.id.del_from_log_relativeLayout);;
	}

}

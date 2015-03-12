package com.example.callhistory.contactdetails;

import com.example.callhistory.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactDetailsActivity extends ActionBarActivity{
	
	private String contactName, contactNumber;
	private TextView contactName_tv, contactnumber_tv, viewContact_tv;
	private RelativeLayout callContact_rl, sendMessage_rl, viewContact_rl, deleteFromLog_rl, addComment_rl, scheduletask_rl;
	private ImageView viewContact_iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_contact_details);
	
	 InitializeUI();
	 
	 contactName = getIntent().getExtras().getString("key_contact_name");
	 contactNumber = getIntent().getStringExtra("key_contact_number");
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
			Intent i = new Intent();     
			i.setAction(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT);
			i.setData(Uri.fromParts("tel", contactNumber, null)); 
			startActivity(i);
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
			contactName_tv.setText(" Call " +contactName2);
			viewContact_iv.setBackgroundResource(R.drawable.ic_action_person);
		}else{
			contactName_tv.setText("Call back");
			viewContact_tv.setText("Add to contacts");
			viewContact_iv.setBackgroundResource(R.drawable.ic_action_add_person);
		}
		
		contactnumber_tv.setText("Number: " +contactNumber2);
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

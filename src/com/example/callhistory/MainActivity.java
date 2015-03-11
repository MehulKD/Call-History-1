package com.example.callhistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.callhistory.database.DatabaseHandler;


public class MainActivity extends Activity {

		private ListView listview = null;
	    private String callType = null;
	    private String phoneNumber = null;
	    private String callDate = null;
	    private String callDuration = null;
	    private String callDateTime = null;
	    private String _phoneNumber;
	    private String contactName = null;
	     
	    private List<CallData> list=new ArrayList<CallData>();
	    private Context context=null;
	    private DatabaseHandler dbHandler = new DatabaseHandler(this);
	    
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_main);
	 context=this;
	 
	 listview=(ListView)findViewById(R.id.listView_calldata);
	  
	  getCallDetails();
	  
	  final CustomAdapter adapter=new CustomAdapter(MainActivity.this, list);
	  listview.setAdapter(adapter);
	  /**
	   * This item long click listener is used to call the delete dialog and read the number from the selected item
	   */
	  listview.setOnItemLongClickListener(new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView num_tv = (TextView) view.findViewById(R.id.callNumber_tv);
			//String text = lv.get(position).tostring().trim();//first method
			String selectedNum = num_tv.getText().toString();//second method
			deleteCommentDialog(selectedNum);
			return false;
		}
		
		/**
		 * This method is used to delete the comment from the database and update the listview of the call register
		 * @param selectedNum
		 */
		private void deleteCommentDialog(final String selectedNum) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setCancelable(false);
	        builder.setMessage("Are you sure, you want to delete comment?");
	        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

	        public void onClick(DialogInterface dialog, int which) {
	         // How to remove the selected item?
	        	dbHandler.deleteComment(selectedNum);
	        	adapter.notifyDataSetChanged();
	        }

	    });
	        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
	        
	     // create alert dialog
			AlertDialog alertDialog = builder.create();

			// show it
			alertDialog.show();

		}
	});
//	  /**
//	   * This listener is used to make call to the clicked contact or num from the call history list
//	   * 
//	   */
//	  listview.setOnItemClickListener(new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			// TODO Auto-generated method stub
//			CallData calldatalist=list.get(position);
//			String callnumber = calldatalist.getCallnumber();
//			Intent callIntent = new Intent(Intent.ACTION_CALL);
//		    callIntent.setData(Uri.parse("tel:" + callnumber));
//		    startActivity(callIntent);
//		}
//	});
	  
	}
	
	
	
	 /**
	  * This method is used to get the call history from the calllog and store that data into a collection 	
	  */
	 public void getCallDetails()
	    {
	        @SuppressWarnings("deprecation")
	        String sortOrder = String.format("%s limit 100 ", CallLog.Calls.DATE + " DESC");
	        Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI, null, null, null, sortOrder);
	        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
	        int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
	        int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
	        int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
 
	          
	        while (managedCursor.moveToNext())
	        {
	          
	            phoneNumber = managedCursor.getString(number);
	            callType = managedCursor.getString(type);
	            callDate = managedCursor.getString(date);
	            contactName = getContactname(phoneNumber);  
	            //callDateTime = new Date(Long.valueOf(callDate));
	            long seconds=Long.parseLong(callDate);
	            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");
	            callDateTime = format1.format(new Date(seconds));
	              
	            callDuration = managedCursor.getString(duration);
	              
	            String cType = null;
	              
	            int cTypeCode = Integer.parseInt(callType);
	              
	            switch(cTypeCode){
	            
	               case CallLog.Calls.OUTGOING_TYPE:
	                   cType = "OUTGOING";
	                   break;
	                  
	               case CallLog.Calls.INCOMING_TYPE:
	                   cType= "INCOMING";
	                   break;
	                  
	               case CallLog.Calls.MISSED_TYPE:
	                   cType = "MISSED";
	                   break;
	                }
	              
	            CallData calldata=new CallData(cType, phoneNumber, contactName, callDateTime, callDuration);
	            list.add(calldata);
	        }
	              
	        managedCursor.close();
	        
	    }
	 
	 /**
	  * this method is used to get the contact name by its phone number
	  * @param phoneNumber2
	  * @return contact name
	  */
	 private String getContactname(String phoneNumber2) {
		// TODO Auto-generated method stub
		 ContentResolver cr = context.getContentResolver();
		    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		    Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		    if (cursor == null) {
		        return null;
		    }
		    String contactName = null;
		    if(cursor.moveToFirst()) {
		        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		    }

		    if(cursor != null && !cursor.isClosed()) {
		        cursor.close();
		    }

		    return contactName;
	}

	@Override
	 public void onResume(){
	     super.onResume();
	     // put your code here...
	 }
	 
	 
	 /**
	  * This Adapter os used to bind the call log collection data to listview 
	  * and giving the functionality of add comment, update comment and delete comment functionality.
	  * @author Srikanth
	  *
	  */
	 public class CustomAdapter extends ArrayAdapter<CallData>{
		 
		 String _heading, _comm;
		 
		 private List<CallData> listdata=null;
		 private LayoutInflater mInflater=null;
		 public CustomAdapter(Activity context, List<CallData> calldata) {
		  super(context, 0);
		  this.listdata=calldata;
		  mInflater = context.getLayoutInflater();
		 }
		 
		  
		 @Override
		 public int getCount() {
		  return listdata.size();
		 }
		  
		  
		 public View getView(final int position, View convertView, ViewGroup parent) {
		   
		  final ViewHolder holder;
		   
		  if (convertView == null || convertView.getTag() == null) {
		   holder = new ViewHolder();
		   convertView = mInflater.inflate(R.layout.list_row, null);
		   
		   holder.calltype = (ImageView) convertView.findViewById(R.id.call_logo_imageView);
		   holder.callnumber = (TextView) convertView.findViewById(R.id.callNumber_tv); 		   
		   holder.calldate = (TextView) convertView.findViewById(R.id.callDate_tv);
		  // holder.callduration = (TextView) convertView.findViewById(R.id.callDuration_tv);
		   holder.heading = (TextView) convertView.findViewById(R.id.heading_tv);
		   holder.addImage = (ImageView) convertView.findViewById(R.id.add_comment_imageView);
		   holder.commentRrlLayout = (RelativeLayout) convertView.findViewById(R.id.comment_rv);
		   holder.numRelLayout = (RelativeLayout) convertView.findViewById(R.id.number_rl);
		         convertView.setTag(holder);
		  }
		  else {
		   holder = (ViewHolder) convertView.getTag();
		  }
		   
		  CallData calldatalist=listdata.get(position);
		  
		  final String callnumber = calldatalist.getCallnumber();
		  String contactname = calldatalist.getContactName();
		  String calltype = calldatalist.getCalltype();
		  String calldate = calldatalist.getCalldatetime();
		  //String callduration=calldatalist.getCallduration();
		  
		  if(calltype == "INCOMING"){
			  holder.calltype.setImageResource(R.drawable.incoming);
		  }if(calltype == "OUTGOING"){
			  holder.calltype.setImageResource(R.drawable.outgoing);
		  }if(calltype == "MISSED"){
			  holder.calltype.setImageResource(R.drawable.missed);
		  }
		   
		  if(null != contactname){
			  holder.callnumber.setText(contactname);
		  }else{
			  holder.callnumber.setText(callnumber);
		  }
		 // holder.calltype.setText(calltype);
		  holder.calldate.setText("Time: " +String.valueOf(calldate));
		  //holder.callduration.setText(callduration+" sec");
		  
		  _comm = dbHandler.getComment(callnumber);
		  _heading = dbHandler.getHeading(callnumber);
		  if(null != _heading){
			  holder.heading.setText(_heading);
			  holder.addImage.setVisibility(View.GONE);
			  holder.heading.setVisibility(View.VISIBLE);
			  
		  }else{
			  holder.heading.setVisibility(View.GONE);
			  holder.addImage.setVisibility(View.VISIBLE);
		  }
		  
		  holder.heading.setText(_heading);
		  
		  holder.addImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = listdata.get(position).getCallnumber();
				addCommentDialog(number);
			}
		});
		  
		  holder.heading.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = listdata.get(position).getCallnumber();
				
				updateCommentDialog(number, dbHandler.getHeading(callnumber), dbHandler.getComment(callnumber));
			}
			
			private void updateCommentDialog(String number, String head, String comme) {
				// TODO Auto-generated method stub
				_phoneNumber = number;
				LayoutInflater inflator = LayoutInflater.from(context);
				View commentView = inflator.inflate(R.layout.comment_layout, null);
				
				final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				alertDialogBuilder.setTitle("Add Comment");
				alertDialogBuilder.setCancelable(false);
				//alert.setMessage("Message");
				
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(commentView);

				// Set an EditText view to get user input 
				final EditText heading_edt = (EditText) commentView.findViewById(R.id.heading_editText);
				final EditText comment_edt = (EditText) commentView.findViewById(R.id.comment_editText);
				heading_edt.setText(head);
				comment_edt.setText(comme);

				alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String heading = heading_edt.getText().toString();
				  String comment = comment_edt.getText().toString();
				  // Do something with values!
				 // Inserting Contacts
			      
			      if(heading.length() > 5){
			    	  
			    	  if(heading.length() < 16){
			    		  
			    		  if(null != dbHandler.getHeading(_phoneNumber)){
			    			  
			    			  Log.d("Insert: ", "Updating .."); 
			    		  
					    	  dbHandler.updateComment(_phoneNumber, heading, comment);
					    	  //_heading = null;
					    	  notifyDataSetChanged();
			    		  }
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
		});
		  
		  holder.numRelLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent callIntent = new Intent(Intent.ACTION_CALL);
			    callIntent.setData(Uri.parse("tel:" + callnumber));
			    startActivity(callIntent);
			}
		});
		  
		  return convertView;
		 }

		 // this method is used to display the dialog when user clicks on add comment textview in list item
		private void addCommentDialog(String number) {
			// TODO Auto-generated method stub
			_phoneNumber = number;
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
			    		  
					      if(null == dbHandler.getHeading(_phoneNumber)){
					    	  
					    	  Log.d("Insert: ", "Inserting .."); 
	
					    	  dbHandler.addContact(_phoneNumber, heading, comment); 
					    	  notifyDataSetChanged();
					      }
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
		  
		}
	 
	 private static class ViewHolder {
		 
	     public TextView callnumber, calldate, callduration, heading;
	     public ImageView addImage, calltype;
	     public RelativeLayout commentRrlLayout, numRelLayout;
	 }
 
}

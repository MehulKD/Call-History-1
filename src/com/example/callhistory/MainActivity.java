package com.example.callhistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.callhistory.database.DatabaseHandler;


public class MainActivity extends Activity {

		private ListView listview = null;
	    private String callType = null;
	    private String phoneNumber = null;
	    private String callDate = null;
	    private String callDuration = null;
	    private Date callDateTime = null;
	    private String _phoneNumber;
	     
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
	  
	  CustomAdapter adapter=new CustomAdapter(MainActivity.this, list);
	  listview.setAdapter(adapter);
	}
	
	 public void getCallDetails()
	    {
	        @SuppressWarnings("deprecation")
	        String sortOrder = String.format("%s limit 100 ", CallLog.Calls.DATE + " DESC");
	  Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, sortOrder);
	      
	        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
	        int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
	        int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
	        int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
	          
	          
	        while (managedCursor.moveToNext())
	        {
	          
	            phoneNumber = managedCursor.getString(number);
	            callType = managedCursor.getString(type);
	            callDate = managedCursor.getString(date);
	              
	            callDateTime = new Date(Long.valueOf(callDate));
	              
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
	              
	            CallData calldata=new CallData(cType, phoneNumber, callDateTime, callDuration);
	            list.add(calldata);
	        }
	              
	        managedCursor.close();
	    }
	 
	 
	 @Override
	 public void onResume(){
	     super.onResume();
	     // put your code here...
	 }
	 
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
		    
		   holder.callnumber = (TextView) convertView.findViewById(R.id.callNumber_tv);
		   holder.calltype = (TextView) convertView.findViewById(R.id.callType_tv);
		   holder.calldate = (TextView) convertView.findViewById(R.id.callDate_tv);
		   holder.callduration = (TextView) convertView.findViewById(R.id.callDuration_tv);
		   holder.addComment = (TextView) convertView.findViewById(R.id.addComment_tv);
		   holder.addImage = (ImageView) convertView.findViewById(R.id.add_comment_imageView);
		         convertView.setTag(holder);
		  }
		  else {
		   holder = (ViewHolder) convertView.getTag();
		  }
		   
		  CallData calldatalist=listdata.get(position);
		  final String callnumber=calldatalist.getCallnumber();
		  String calltype=calldatalist.getCalltype();
		  Date calldate= calldatalist.getCalldatetime();
		  String callduration=calldatalist.getCallduration();
		  
		  if(calltype == "INCOMING"){
			  holder.calltype.setTextColor(Color.GREEN);
		  }if(calltype == "OUTGOING"){
			  holder.calltype.setTextColor(Color.BLUE);
		  }if(calltype == "MISSED"){
			  holder.calltype.setTextColor(Color.RED);
		  }
		   
		  holder.callnumber.setText(callnumber);
		  holder.calltype.setText(calltype);
		  holder.calldate.setText(String.valueOf(calldate));
		  holder.callduration.setText(callduration+" sec");
		  
		  holder.addImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = listdata.get(position).getCallnumber();
				addCommentDialog(number);
			}
		});
		  
		  holder.addComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = listdata.get(position).getCallnumber();
				
				updateCommentDialog(number, _heading = dbHandler.getHeading(callnumber), dbHandler.getComment(callnumber));
			}

			private void updateCommentDialog(String number, String head, String comme) {
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
				heading_edt.setText(head);
				comment_edt.setText(comme);

				alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String heading = heading_edt.getText().toString();
				  String comment = comment_edt.getText().toString();
				  // Do something with values!
				 // Inserting Contacts
			      Log.d("Insert: ", "Inserting .."); 
				  //dbHandler.addContact(new Contact(name, _phoneNumber, heading, comment));
			      if(heading.length() > 5){
			    	  
			    	  if(heading.length() < 16){
			    		  
					    	  dbHandler.updateComment(_phoneNumber, heading, comment);
					    	  _heading = null;
					    	  notifyDataSetChanged();
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
		  
		  _comm = dbHandler.getComment(callnumber);
		  _heading = dbHandler.getHeading(callnumber);
		  if(null != _heading){
			  holder.addComment.setText(_heading);
			  holder.addImage.setVisibility(View.GONE);
			  holder.addComment.setVisibility(View.VISIBLE);
			  
		  }else{
			  holder.addComment.setVisibility(View.GONE);
			  holder.addImage.setVisibility(View.VISIBLE);
		  }
		  
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
		      Log.d("Insert: ", "Inserting .."); 
			  //dbHandler.addContact(new Contact(name, _phoneNumber, heading, comment));
		      if(heading.length() > 5){
		    	  
		    	  if(heading.length() < 16){
		    		  
				      if(null == dbHandler.getHeading(_phoneNumber)){
				    	  
//				    	  dbHandler.updateComment(_phoneNumber, heading, comment);
//				    	  _heading = null;
//				      }else{
				    	  
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
		 
	     public TextView callnumber, calltype, calldate, callduration, addComment;
	     public ImageView addImage;
	 }

 
}

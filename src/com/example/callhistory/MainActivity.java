package com.example.callhistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.callhistory.contactdetails.ContactDetailsActivity;
import com.example.callhistory.model.GridListDataModel;
import com.example.callhistory.swipetocall.SwipeActionAdapter;
import com.example.callhistory.swipetocall.SwipeDirections;


public class MainActivity extends Activity implements SwipeActionAdapter.SwipeActionListener {

		private ListView listview = null;
		private GridView gridView;
		private TextView grid_num_tv;
		private EditText phone_num_edt;
		private RelativeLayout gridView_collapse_rl, dialPad_overflow_rl, dialPad_num_edt_rl;
		private ImageView dialPad_collapse_iv, backspace_iv;
		Cursor managedCursor;
		//private Spinner spinner;
		private ArrayList<String> number = new ArrayList<String>();
		static final String[] GRID_NUM = new String[] {
			"1", "2", "3", 
			"4", "5", "6", 
			"7" ,"8", "9" ,
			"*", "0", "#"};
		static final String[] GRID_LETTERS = new String[] {
			" ", "abc", "def", 
			"ghi", "jkl", "mno", 
			"pqrs" ,"tuv", "wxyz" ,
			" ", "+", " "};
		ArrayList<GridListDataModel> myGridList = new ArrayList<GridListDataModel>();

	    private String callType = null;
	    private String phoneNumber = null;
	    private String callDate = null;
	    private String callDuration = null;
	    private String callDateTime = null;
	    private String _phoneNumber;
	    private String contactName = null;
	    
	    private List<CallData> list=new ArrayList<CallData>();
	    public Context context=null;
	    
	    protected SwipeActionAdapter mAdapter;
	    
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_main);
	 context=this;
	 
	  hideSoftKeyboard();
	 
	  InitializeUI();
	  
	  getCallDetails();
	  
	  final CustomAdapter adapter=new CustomAdapter(MainActivity.this, list);
	  
	  mAdapter = new SwipeActionAdapter(adapter);
      mAdapter.setSwipeActionListener(this).setListView(listview);
      //setListAdapter(mAdapter);

      mAdapter.addBackground(SwipeDirections.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
              .addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
              .addBackground(SwipeDirections.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
              .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);
      
	  listview.setAdapter(mAdapter);

      
      dialPad_num_edt_rl = (RelativeLayout) findViewById(R.id.dial_num_edt_rl);
      
      gridView.setVisibility(View.VISIBLE);
      
      gridView.setAdapter(new CustomGridAdapter(this, GRID_NUM));
      
      
      gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				grid_num_tv = (TextView) view.findViewById(R.id.grid_num_textView);
				String num = phone_num_edt.getText().toString() + grid_num_tv.getText().toString();
				
				number.add(grid_num_tv.getText().toString());
				phone_num_edt.setText(num);
				// to place the edittext cursor always right side of the entered number
				phone_num_edt.setSelection(phone_num_edt.getText().length());
			}
		});
		
		gridView_collapse_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// dont forget to put android:animateLayoutChanges="true" in your xml container layout
				if (gridView.getVisibility() == View.VISIBLE) {
					
					gridView.setVisibility(View.GONE);
					dialPad_collapse_iv.setImageResource(R.drawable.ic_action_collapse);
					
              }else{
              	
            	  	gridView.setVisibility(View.VISIBLE);
            	  	dialPad_collapse_iv.setImageResource(R.drawable.ic_action_expand);
              }
				
			}
		});
		
		phone_num_edt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				return true;
			}
		});
		
		backspace_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(0 != phone_num_edt.getText().length()){
					// To delete the last character in edittext
					phone_num_edt.getText().delete(phone_num_edt.getText().length() - 1,
							phone_num_edt.getText().length());
				}
				
			}
		});
		/**
		 * This listener is used to hide the keypad when user scrollup the listview
		 */
		listview.setOnScrollListener(new OnScrollListener() {
			
			private int mLastFirstVisibleItem;
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				final ListView lw = (ListView) view.findViewById(R.id.listView_calldata);

			       if(scrollState == 0) 
			      Log.i("a", "scrolling stopped...");


			        if (view.getId() == lw.getId()) {
			        final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
			         if (currentFirstVisibleItem > mLastFirstVisibleItem) {
			            //mIsScrollingUp = false;
			            Log.i("a", "scrolling down...");
			            
			            if (gridView.getVisibility() == View.VISIBLE) {
							
							gridView.setVisibility(View.GONE);
							dialPad_collapse_iv.setImageResource(R.drawable.ic_action_collapse);
			            }
 
			        } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
			            //mIsScrollingUp = true;
			            Log.i("a", "scrolling up...");
			            
		              
			        }

			        mLastFirstVisibleItem = currentFirstVisibleItem;
			    } 
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * Hides the soft keyboard
	 */
	 private void hideSoftKeyboard() {
		// TODO Auto-generated method stub
		 
		 if(getCurrentFocus()!=null) {
		        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		    }
		
	}

	private void InitializeUI() {
		// TODO Auto-generated method stub
		 listview=(ListView)findViewById(R.id.listView_calldata);
		 gridView = (GridView) findViewById(R.id.gridView1);
	     phone_num_edt = (EditText) findViewById(R.id.dial_pad_num_editText);
	     gridView_collapse_rl = (RelativeLayout) findViewById(R.id.dial_close_rl);
	     //dialPad_overflow_rl = (RelativeLayout) findViewById(R.id.dial_overflow_rl);
	     dialPad_collapse_iv = (ImageView) findViewById(R.id.dialpad_collapse_imageView);
	     backspace_iv = (ImageView) findViewById(R.id.backspace_imageView);
	     //spinner = (Spinner) findViewById(R.id.spinner1);
	}

	
	/**
	  * This method is used to get the call history from the calllog and store that data into a collection 	
	  */
	 public void getCallDetails()
	    {
	        @SuppressWarnings("deprecation")
	        String sortOrder = String.format("%s limit 100 ", CallLog.Calls.DATE + " DESC");
	        managedCursor = managedQuery( CallLog.Calls.CONTENT_URI, null, null, null, sortOrder);
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
	            //contactId = getContactId(phoneNumber);
	            
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
	              
	       // managedCursor.close();
	        
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
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        managedCursor.close();
    }
	 
	 
	 /**
	  * This Adapter os used to bind the call log collection data to listview 
	  * and giving the functionality of add comment, update comment and delete comment functionality.
	  * @author Srikanth
	  *
	  */
	 public class CustomAdapter extends ArrayAdapter<CallData>{
		 
		 //String _heading, _comm;
		 
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
		   //holder.heading = (TextView) convertView.findViewById(R.id.heading_tv);
		   //holder.addImage = (ImageView) convertView.findViewById(R.id.add_comment_imageView);
		   holder.contactDetails_rl = (RelativeLayout) convertView.findViewById(R.id.comment_rv);
		   holder.numRelLayout = (RelativeLayout) convertView.findViewById(R.id.number_rl);
		   holder.nextImage = (ImageView) convertView.findViewById(R.id.next_imageView);
		         convertView.setTag(holder);
		  }
		  else {
		   holder = (ViewHolder) convertView.getTag();
		  }
		   
		  CallData calldatalist=listdata.get(position);
		  
		  final String callnumber = calldatalist.getCallnumber();
		  final String contactname = calldatalist.getContactName();
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
		  
		  
		  holder.contactDetails_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = listdata.get(position).getCallnumber();
				Intent detailsActInt = new Intent(MainActivity.this, ContactDetailsActivity.class);
				detailsActInt.putExtra("key_contact_name", contactname);
				detailsActInt.putExtra("key_contact_number", callnumber);
				startActivity(detailsActInt);
				//addCommentDialog(number);
			}
		});
		  

		  
		  /**
		   * This listener is used to make call to the clicked contact or num from the call history list
		   * 
		   */
		  holder.numRelLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
//				Intent callIntent = new Intent(Intent.ACTION_CALL);
//			    callIntent.setData(Uri.parse("tel:" + callnumber));
//			    startActivity(callIntent);
			}
		});
		  

		  
		  return convertView;
		  
		 }

	  
		}
	 
	 private static class ViewHolder {
		 
	     public TextView callnumber, calldate, callduration, heading;
	     public ImageView calltype, nextImage;
	     public RelativeLayout contactDetails_rl, numRelLayout;
	 }

	@Override
	public boolean hasActions(int position) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldDismiss(int position, int direction) {
		// TODO Auto-generated method stub
		return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
	}

	@Override
	public void onSwipe(int[] positionList, int[] directionList) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<positionList.length;i++) {
            int direction = directionList[i];
            int position = positionList[i];
            String dir = "";

            switch (direction) {
                case SwipeDirections.DIRECTION_FAR_LEFT:
                    dir = "Far left";
                    break;
                case SwipeDirections.DIRECTION_NORMAL_LEFT:
                    dir = "Left";
                    break;
                case SwipeDirections.DIRECTION_FAR_RIGHT:
                    dir = "Far right";
                    break;
                case SwipeDirections.DIRECTION_NORMAL_RIGHT:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();
                    dir = "Right";
                    break;
            }
//            Toast.makeText(
//                    this,
//                    dir + " swipe Action triggered on " + mAdapter.getItem(position),
//                    Toast.LENGTH_SHORT
//            ).show();
            mAdapter.notifyDataSetChanged();
        }
		
	}
	
 
}

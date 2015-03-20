package com.example.callhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomGridAdapter extends BaseAdapter{

	private Context context;
	private final String[] numValues;
	
	public CustomGridAdapter(Context context, String[] gridNum) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.numValues = gridNum;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return numValues.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View gridView;
	 
			if (convertView == null) {
	 
				gridView = new View(context);
	 
				// get layout from mobile.xml
				gridView = inflater.inflate(R.layout.grid_item, null);
	 
				// set value into textview
				TextView num_textView = (TextView) gridView
						.findViewById(R.id.grid_num_textView);
				num_textView.setText(numValues[position]);
	 
				// set image based on selected text
				TextView letter_textView = (TextView) gridView
						.findViewById(R.id.grid_letter_textView);
	 
				String mobile = numValues[position];
	 
				if (mobile.equals("1")) {
					letter_textView.setText(" ");
				} else if (mobile.equals("2")) {
					letter_textView.setText("abc");
				} else if (mobile.equals("3")) {
					letter_textView.setText("def");
				} else if (mobile.equals("4")) {
						letter_textView.setText("ghi");
				}  else if (mobile.equals("5")) {
					letter_textView.setText("jkl");
				} else if (mobile.equals("6")) {
					letter_textView.setText("mno");
				} else if (mobile.equals("7")) {
					letter_textView.setText("pqrs");
				} else if (mobile.equals("8")) {
					letter_textView.setText("tuv");
				} else if (mobile.equals("9")) {
					letter_textView.setText("wxyz");
				} else if (mobile.equals("*")) {
					letter_textView.setText(" ");
				} else if (mobile.equals("0")) {
					letter_textView.setText("+");
				} else if (mobile.equals("#")) {
					letter_textView.setText(" ");
				}else {
					letter_textView.setText(" ");
				}
	 
			} else {
				gridView = (View) convertView;
			}
			
			return gridView;
	}

	
}

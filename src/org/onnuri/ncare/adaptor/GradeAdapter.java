package org.onnuri.ncare.adaptor;

import java.util.ArrayList;

import org.onnuri.ncare.R;
import org.onnuri.ncare.vo.GradeData;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GradeAdapter extends ArrayAdapter<GradeData> {
	
	private Activity activity;
	private ArrayList<GradeData> mItems;
	private LayoutInflater layoutInflater;
		
	public GradeAdapter(Activity activity, ArrayList<GradeData> items) {
		super(activity, 0, items);
		
		this.activity = activity;
		this.mItems = items;
		this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GradeData item = mItems.get(position);

		ViewHolder vh;
		View v = convertView;

		if (v == null) {
			v = layoutInflater.inflate(R.layout.list_item_data, null);
			
	        vh = new ViewHolder();
	        vh.titleTextView = (TextView) v.findViewById(R.id.textViewTitle);
	        
	        v.setTag(vh);
		}
		
	    vh = (ViewHolder) v.getTag();
        vh.titleTextView.setText(item.title);
        
		return v;
	}
	
	static class ViewHolder {
		TextView titleTextView;
	}
}
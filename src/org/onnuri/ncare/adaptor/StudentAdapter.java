package org.onnuri.ncare.adaptor;

import java.util.ArrayList;

import org.onnuri.ncare.AttendanceActivity;
import org.onnuri.ncare.R;
import org.onnuri.ncare.vo.StudentData;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class StudentAdapter extends ArrayAdapter<StudentData> {
	
	private Activity activity;
	private ArrayList<StudentData> mItems;
	private LayoutInflater layoutInflater;
		
	public StudentAdapter(Activity activity, ArrayList<StudentData> items) {
		super(activity, 0, items);
		
		this.activity = activity;
		this.mItems = items;
		this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		StudentData item = mItems.get(position);

		ViewHolder vh;
		View v = convertView;

		if (v == null) {
			v = layoutInflater.inflate(R.layout.list_item_student, null);
			
	        vh = new ViewHolder();
	        vh.attendanceCheckBox = (CheckBox) v.findViewById(R.id.checkBoxAttendance);
	        vh.visitRequestCheckBox = (CheckBox) v.findViewById(R.id.checkBoxVisitRequest);
	        vh.nameTextView = (TextView) v.findViewById(R.id.textViewName);
	        vh.birthTextView = (TextView) v.findViewById(R.id.textViewBirth);
	        vh.memoTextView = (EditText) v.findViewById(R.id.editTextMemo);

	        v.setTag(vh);
		}
		
	    vh = (ViewHolder) v.getTag();
	    
	    vh.attendanceCheckBox.setChecked("Y".equals(item.attend_yn));
        vh.attendanceCheckBox.setTag(item);
        vh.attendanceCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox checkBox = (CheckBox) v;
				StudentData item = (StudentData) v.getTag();
				if (checkBox.isChecked()) {
					item.attend_yn = "Y";
					item.sau = null;
				} else {
					item.attend_yn= "N";
					// 결석 사유 선택 창 표시 필요 ???
					item.sau = "늦잠 때문에 결석했습니다.";
					((AttendanceActivity)activity).setAbsenceCause(item);
				}
				notifyDataSetChanged();
			}
		});
        
        vh.visitRequestCheckBox.setChecked("Y".equals(item.simbang_yn));
        vh.visitRequestCheckBox.setTag(item);
        vh.visitRequestCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox checkBox = (CheckBox) v;
				StudentData item = (StudentData) v.getTag();
				if (checkBox.isChecked()) {
					item.simbang_yn = "Y";
				} else {
					item.simbang_yn = "N";
				}
				notifyDataSetChanged();
			}
		});
        
        vh.nameTextView.setText(item.name);
        
        vh.birthTextView.setText(" (" + item.birth + ")");
        
        vh.memoTextView.setText(item.sau);
        
		return v;
	}
	
	static class ViewHolder {
		CheckBox attendanceCheckBox;
		CheckBox visitRequestCheckBox;
		TextView nameTextView;
		TextView birthTextView;
		EditText memoTextView;
	}
}
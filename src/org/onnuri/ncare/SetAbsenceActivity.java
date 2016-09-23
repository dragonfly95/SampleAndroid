package org.onnuri.ncare;

import java.util.ArrayList;

import org.onnuri.ncare.vo.StudentData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SetAbsenceActivity extends Activity {

	static final String TAG = SetAbsenceActivity.class.getSimpleName();
	
	TextView titleTextView;
	EditText causeEditText;
	Button causeButton;
	ListView mListView;
	ArrayAdapter<String> mAdapter;
	ArrayList<String> mItems;
	StudentData studentData = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_absence);
		
		Intent intent = getIntent();
		studentData = intent.getParcelableExtra("STUDENT");
		
		titleTextView = (TextView) findViewById(R.id.textViewTitle);
		causeEditText = (EditText) findViewById(R.id.editTextCause);
		causeButton = (Button) findViewById(R.id.buttonCause);
		causeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String cause = causeEditText.getText().toString();
				studentData.sau = cause;
				
				Intent intent = new Intent();
				intent.putExtra("STUDENT", studentData);
				setResult(Activity.RESULT_OK, intent);
				finish();
				
			}
		});
		
		mListView = (ListView) findViewById(R.id.listViewAbsence);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String cause = mItems.get(position);
				studentData.sau = cause;
				
				Intent intent = new Intent();
				intent.putExtra("STUDENT", studentData);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		
		// 화면 표시 
		if (studentData != null) {
			String titleString = String.format("%s 학생의 결석 사유", studentData.name);
			titleTextView.setText(titleString);		
			causeEditText.setText(studentData.sau);
		}
		
		// 결석사유 아이템 가져오기 
		getAbsence();
	}
	
	void getAbsence() {
		mItems = new ArrayList<String>();
		
		mItems.add("사유를 알지 못하는 결석");
		mItems.add("늦잠 등의 단순 사유 결석");
		mItems.add("병으로 인한 결석");
		mItems.add("시험, 학원 등으로 인한 결석");
		mItems.add("가정사로 인한 결석");
		mItems.add("다른교회 참석");
		mItems.add("인정되지 않은 다른예배 출석");
		mItems.add("언어연수 등의 장기 연수로 인한 결석");
		
		if (mItems != null) {
			 mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItems);
			 mListView.setAdapter(mAdapter);
		}
	}
	
}

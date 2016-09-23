package org.onnuri.ncare;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onnuri.ncare.adaptor.ClassAdapter;
import org.onnuri.ncare.vo.ClassData;
import org.onnuri.ncare.vo.GradeData;
import org.onnuri.ncare.webservice.HttpManager;
import org.onnuri.ncare.webservice.OnResultListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


public class ClassActivity extends Activity {
	static final String TAG = ClassActivity.class.getSimpleName();

	TextView titleTextView;
	ListView mListView;
	ClassAdapter mAdapter;
	ArrayList<ClassData> mItems;
	GradeData mGradeData = null;			// 학년 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class);
	
		// 메인에서 전달된 정보 가져오기 
		Intent intent= getIntent();
		if (intent != null) {
			mGradeData = intent.getParcelableExtra(MainActivity.GRAGE_DATA);
		}
		
		// 타이틀 뷰 
		titleTextView = (TextView) findViewById(R.id.textViewTitle);
		titleTextView.setText(mGradeData.division.campus.title + "/" +
							  mGradeData.division.title + "/" +
							  mGradeData.title + " " +
							  "반 선택");
		
		// 반 리스트 
		mListView = (ListView) findViewById(R.id.listViewClass);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ClassData selectedClass = mItems.get(position);
				
				if (selectedClass != null) {
					selectedClass.grade = mGradeData;

					// 데이터 전달과 함께 종료 
					Intent i = new Intent();
					i.putExtra(MainActivity.CLASS_DATA, selectedClass);
					setResult(Activity.RESULT_OK, i);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}
			}
		});
		
		// 반 정보 가져오기 
		getClassList(mGradeData);
		
	}
	
	/**
	 * 반 리스트 가져오기 
	 * - 주어진 학년 정보를 기준으로 서버에서 반 리스트를 가져온다. 
	 * 
	 * @param GradeData
	 */
	void getClassList(GradeData gradeData) {
		HttpManager manager = HttpManager.sharedInstance();
		manager.getClass(gradeData.division.campus.title,
						 gradeData.division.title, 
						 gradeData.title, 
						 new OnResultListener() {
			@Override
			public void onCompletion(String result) {
				Log.i(TAG, "result " + result);
				if (result != null) {
					try {
						JSONArray jsonArray = new JSONArray(result);
						if (jsonArray != null) {
							mItems = new ArrayList<ClassData>();
							JSONObject json = null;
							
							for (int i=0; i<jsonArray.length(); i++) {
								ClassData data = new ClassData();
								json = (JSONObject) jsonArray.get(i);
								
								if (json != null) {
									data.code = json.getString("COMMCODE");
									data.title = json.getString("SOON");
									mItems.add(data);
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						mItems = null;
					}
					
					// 학년 정보 화면에 표시 
					if (mItems != null) {
						mAdapter = new ClassAdapter(ClassActivity.this, mItems);
						mListView.setAdapter(mAdapter);
					}
				} else {
					// 에러 처리
				}
			}
		});
	}
}

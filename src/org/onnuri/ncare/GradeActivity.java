package org.onnuri.ncare;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onnuri.ncare.adaptor.GradeAdapter;
import org.onnuri.ncare.vo.DivisionData;
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


public class GradeActivity extends Activity {
	static final String TAG = GradeActivity.class.getSimpleName();	
	
	TextView titleTextView;
	ListView mListView;
	GradeAdapter mAdapter;
	ArrayList<GradeData> mItems;
	DivisionData mDivisionData = null;		// 부서
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grade);
	
		// 메인에서 전달된 정보 가져오기 
		Intent intent= getIntent();
		if (intent != null) {
			mDivisionData = intent.getParcelableExtra(MainActivity.DIVISION_DATA);
		}
		
		// 타이틀 뷰 
		titleTextView = (TextView) findViewById(R.id.textViewTitle);
		titleTextView.setText(mDivisionData.campus.title + "/" +
							  mDivisionData.title + " " +
							  "학년 선택");
		
		// 학년 리스트 
		mListView = (ListView) findViewById(R.id.listViewGrade);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GradeData selectedGradeData = mItems.get(position);
				
				if (selectedGradeData != null) {
					selectedGradeData.division = mDivisionData;

					// 데이터 전달과 함께 종료 
					Intent i = new Intent();
					i.putExtra(MainActivity.GRAGE_DATA, selectedGradeData);
					setResult(Activity.RESULT_OK, i);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}				
			}
		});
		
		// 학년 정보 가져오기 
		getGradeList(mDivisionData);
		
	}
	
	/**
	 * 학년 리스트 가져오기 
	 * - 주어진 부서 정보를 기준으로 서버에서 학년 리스트를 가져온다. 
	 * 
	 * @param DivisionData 캠퍼스, 부서 정보
	 */
	void getGradeList(DivisionData divisionData) {
		HttpManager manager = HttpManager.sharedInstance();
		manager.getGrade(divisionData.campus.title, 
						 divisionData.title, 
						 new OnResultListener() {
			@Override
			public void onCompletion(String result) {
				Log.i(TAG, "result " + result);
				if (result != null) {
					try {
						JSONArray jsonArray = new JSONArray(result);
						if (jsonArray != null) {
							mItems = new ArrayList<GradeData>();
							JSONObject json = null;
							
							for (int i=0; i<jsonArray.length(); i++) {
								GradeData data = new GradeData();
								json = (JSONObject) jsonArray.get(i);
								if (json != null) {
									data.code = json.getString("COMMCODE");
									data.title = json.getString("DLB");
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
						mAdapter = new GradeAdapter(GradeActivity.this, mItems);
						mListView.setAdapter(mAdapter);
					}
				} else {
					// 에러 처리
				}
			}
		});
	}
}

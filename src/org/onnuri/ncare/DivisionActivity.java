package org.onnuri.ncare;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onnuri.ncare.adaptor.DivisionAdapter;
import org.onnuri.ncare.vo.CampusData;
import org.onnuri.ncare.vo.DivisionData;
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


public class DivisionActivity extends Activity {
	static final String TAG = DivisionActivity.class.getSimpleName();
	
	TextView titleTextView;
	ListView mListView;
	DivisionAdapter mAdapter;
	ArrayList<DivisionData> mItems;
	CampusData mCampusData;				// 캠퍼스 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_division);

		// 메인에서 전달된 정보 가져오기 
		Intent intent= getIntent();
		if (intent != null) {
			mCampusData = intent.getParcelableExtra(MainActivity.CAMPUS_DATA);
		}
		
		// 타이틀 뷰 
		titleTextView = (TextView) findViewById(R.id.textViewTitle);
		titleTextView.setText(mCampusData.title + " 부서 선택");
		
		// 리스트 
		mListView = (ListView) findViewById(R.id.listViewDivision);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DivisionData selectedDivision = mItems.get(position);
				
				if (selectedDivision != null) {
					selectedDivision.campus = mCampusData;

					// 데이터 전달과 함께 종료 
					Intent i = new Intent();
					i.putExtra(MainActivity.DIVISION_DATA, selectedDivision);
					setResult(Activity.RESULT_OK, i);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}				
			}
		});
		
		// 부서 정보 가져오기 
		getDivisionList(mCampusData);
		
	}
	
	/**
	 * 부서 리스트 가져오기 함수 
	 * - 주어진 캠퍼스 정보를 기준으로 서버에서 부서 리스트를 가져온다. 
	 * 
	 * @param campusData
	 */
	void getDivisionList(CampusData campusData) {
		HttpManager manager = HttpManager.sharedInstance();
		manager.getDivision(campusData.title, new OnResultListener() {
			@Override
			public void onCompletion(String result) {
				Log.i(TAG, "result " + result);
				if (result != null) {
					try {
						JSONArray jsonArray = new JSONArray(result);
						if (jsonArray != null) {
							mItems = new ArrayList<DivisionData>();
							JSONObject json = null;
							
							for (int i=0; i<jsonArray.length(); i++) {
								DivisionData data = new DivisionData();
								json = (JSONObject) jsonArray.get(i);
								
								if (json != null) {
									data.code = json.getString("COMMCODE");
									data.title = json.getString("COMM");
									mItems.add(data);
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						mItems = null;
					}
					
					// 캠퍼스 정보 화면에 표시 
					if (mItems != null) {
						mAdapter = new DivisionAdapter(DivisionActivity.this, mItems);
						mListView.setAdapter(mAdapter);
					}
				} else {
					// 에러 처리
				}
			}
		});
	}
}

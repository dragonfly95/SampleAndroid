package org.onnuri.ncare;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onnuri.ncare.adaptor.CampusAdapter;
import org.onnuri.ncare.vo.CampusData;
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


public class CampusActivity extends Activity {
	static final String TAG = CampusActivity.class.getSimpleName();
	
	TextView titleTextView;
	ListView mListView;
	CampusAdapter mAdapter;
	ArrayList<CampusData> mItems;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus);
	
		// 타이틀 뷰 
		titleTextView = (TextView) findViewById(R.id.textViewTitle);
		titleTextView.setText("캠퍼스 선택");
		
		// 리스트 
		mListView = (ListView) findViewById(R.id.listViewCampus);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CampusData selectedCampus = mItems.get(position);
				
				if (selectedCampus != null) {
					// 데이터 전달과 함께 종료 
					Intent i = new Intent();
					i.putExtra(MainActivity.CAMPUS_DATA, selectedCampus);
					setResult(Activity.RESULT_OK, i);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}				
			}
		});
		
		// 캠퍼스 정보 가져오기 
		getCampusList();
		
	}
	
	/**
	 * 캠퍼스 리스트 가져오기 함수 
	 */
	void getCampusList() {
		HttpManager manager = HttpManager.sharedInstance();
		manager.getCampus(new OnResultListener() {
			@Override
			public void onCompletion(String result) {
				Log.i(TAG, "result " + result);
				if (result != null) {
					try {
						JSONArray jsonArray = new JSONArray(result);
						if (jsonArray != null) {
							mItems = new ArrayList<CampusData>();
							JSONObject json = null;
							
							for (int i=0; i<jsonArray.length(); i++) {
								CampusData data = new CampusData();
								json = (JSONObject) jsonArray.get(i);
								
								if (json != null) {
									data.code = json.getString("COMMCODE");
									data.title = json.getString("KK");
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
						mAdapter = new CampusAdapter(CampusActivity.this, mItems);
						mListView.setAdapter(mAdapter);
					}
				} else {
					// 에러 처리
				}
			}
		});
	}
}

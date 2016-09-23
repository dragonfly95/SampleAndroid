package org.onnuri.ncare;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onnuri.ncare.adaptor.CampusAdapter;
import org.onnuri.ncare.vo.CampusData;
import org.onnuri.ncare.vo.ClassData;
import org.onnuri.ncare.vo.DivisionData;
import org.onnuri.ncare.vo.GradeData;
import org.onnuri.ncare.webservice.HttpManager;
import org.onnuri.ncare.webservice.OnResultListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	static final String TAG = LoginActivity.class.getSimpleName();

	EditText idEditText, pwEditText;
	Button loginButton;
	
	String mId, mPw;
	ClassData mClassdata = null;
	
	String userid = "nosignal95";
	String passwd = "acts29!@";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// 아이디 입력화면 
		idEditText = (EditText) findViewById(R.id.editTextId);
		
		// 패스워트 입력화면 
		pwEditText = (EditText) findViewById(R.id.editTextPw);
		
		// 로그인 버튼 
		loginButton = (Button) findViewById(R.id.buttonLogin);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mId = idEditText.getText().toString();
				mPw = pwEditText.getText().toString();
				
				login(mId, mPw);
			}
		});
		
		idEditText.setText(userid);
		pwEditText.setText(passwd);
	}

	/**
	 * 로그인 함수
	 * - 서버에 로그인 하는 함수  
	 * @param id
	 * @param pw
	 **/
	void login(String id, String pw) {
		// 서버에 로그인후 사용자 정보를 가져온다. 
		
		HttpManager manager = HttpManager.sharedInstance();
		manager.login(mId, mPw, new OnResultListener() {

			@Override
			public void onCompletion(String result) {
				Log.i(TAG, "result " + result);
				
				try {
					JSONObject json = new JSONObject(result);
					CampusData campusData = new CampusData();			// 캠퍼스
					String kk = json.getString("kk");
					if (kk.equals("null")) {
						campusData.title = null;
					} else {
						campusData.title = kk;
					}
					
					DivisionData divisionData = new DivisionData();		// 부서 
					divisionData.campus = campusData;
					String comm = json.getString("comm");
					if (comm.equals("null")) {
						divisionData.title = null;
					} else {
						divisionData.title = comm;
					}
					
					GradeData gradeData = new GradeData();				// 학년
					gradeData.division = divisionData;
					String dlb = json.getString("dlb");
					if (dlb.equals("null")) {
						gradeData.title = null;
					} else {
						gradeData.title = dlb;
					}
							
					mClassdata = new ClassData();
					mClassdata.grade = gradeData;
					String soon = json.getString("soon");
					if (soon.equals("null")) {
						mClassdata.title = null;
					} else {
						mClassdata.title = dlb;
					}
					
					NC_Preferences.name = json.getString("name");
					
					// 로그인 성공시 메인 화면 이동  
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					intent.putExtra(MainActivity.CLASS_DATA, mClassdata);
					startActivity(intent);
					
					// 종료 
					finish();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}  // end onCompletion
			
		});   // end login method
		
		
	}
}

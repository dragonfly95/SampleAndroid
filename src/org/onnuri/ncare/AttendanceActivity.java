package org.onnuri.ncare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onnuri.ncare.adaptor.StudentAdapter;
import org.onnuri.ncare.vo.ClassData;
import org.onnuri.ncare.vo.StudentData;
import org.onnuri.ncare.webservice.HttpManager;
import org.onnuri.ncare.webservice.OnResultListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class AttendanceActivity extends Activity {
	
	static final int REQUEST_CODE_SET_ABSENCE = 0x01;
	
	String TAG = AttendanceActivity.class.getSimpleName();
	
	TextView classTitleTextView, dateTextView;
	Button cancelButton, finishButton;
	ListView mListView;
	StudentAdapter mAdapter;
	ArrayList<StudentData> mItems;
	ClassData mClassData = null;			// 반 
	Calendar mToday;
	
	String mBogoDay;
	boolean isUpdate = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance);
	
		Intent intent= getIntent();
		if (intent != null) {
			mClassData = intent.getParcelableExtra(MainActivity.CLASS_DATA);			
		}
		
		// 타이틀 뷰 
		classTitleTextView = (TextView) findViewById(R.id.textViewClassTitle);
		classTitleTextView.setText(mClassData.grade.division.campus.title + "/" +
								   mClassData.grade.division.title + "/" +
								   mClassData.grade.title + "/" +
								   mClassData.title);

		// 날짜 뷰 
		dateTextView = (TextView) findViewById(R.id.textViewDate);
		mToday = Calendar.getInstance();
		setDateOnView(mToday, dateTextView);
		
		// 학생 리스트 
		mListView = (ListView) findViewById(R.id.listViewAttendance);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//
				
			}
		});
		
		// 취소 버튼 
		cancelButton = (Button) findViewById(R.id.buttonCancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 취소  
				finish();
				overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_down);
			}
		});
		
		// 출석체크 완료 버튼 
		finishButton = (Button) findViewById(R.id.buttonCheckAttendance);
		finishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 출결 정보를 저장한 후 종료한다.
//				if (isUpdate) {
//					updateStudents(mItems);
//				} else {
//					addStudents(mItems);
//				}
				
				
				String jsonString = null;
				try {
					jsonString = toJSONStringForAdd(mItems);
					Log.d(TAG, jsonString);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		// 학생 정보 가져오기 
		getStudentList(mClassData);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_SET_ABSENCE) {
			if (resultCode == Activity.RESULT_OK) {
				StudentData studentData = data.getParcelableExtra("STUDENT"); 
				
				for (int i=0; i<mItems.size(); i++) {
					if (mItems.get(i).name.equals(studentData.name) && mItems.get(i).birth.equals(studentData.birth)) {
						mItems.get(i).sau = studentData.sau;
						break;
					}
				}
				
				mAdapter.notifyDataSetChanged();
			}
		}
		
		//super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	String[] days = {"", "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
	
	/**
	 * 날짜 표시 함수 
	 * 
	 * @param calendar
	 * @param textView
	 */
	void setDateOnView(Calendar calendar, TextView textView) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		String dayOfWeekString = days[dayOfWeek];
        
		String todayString = String.format(Locale.KOREA, "%d년 %d월 %d일 (%s)", year, month+1, day, dayOfWeekString);
        textView.setText(todayString);
	}
	
	/**
	 * 학생 정보 가져오기 함수 
	 * -  주어진 반 정보를 기준으로 서버에서 학생 정보를 가져온다. 
	 * 
	 * @param classData
	 */
	void getStudentList(final ClassData classData) {
		// 서버에서 학생 정보 가져오기 
		HttpManager manager = HttpManager.sharedInstance();
		manager.getStudent(classData.code, new OnResultListener() {
			@Override
			public void onCompletion(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						mBogoDay = json.getString("bogoday");
//								
						JSONArray jsonArray = json.getJSONArray("list");
						if (jsonArray != null) {
							mItems = new ArrayList<StudentData>();
							JSONObject studentJson = null;
							for (int i=0; i<jsonArray.length(); i++) {
								StudentData data = new StudentData();
								data.classInfo = classData;
								studentJson = (JSONObject) jsonArray.get(i);								
								if (studentJson != null) {
									data.birth = studentJson.getString("birth");
									data.wdiv = studentJson.getString("wdiv");
									data.pass_yn = studentJson.getString("pass_yn");
									data.commcode = studentJson.getString("commcode");
									data.dlb = studentJson.getString("dlb");
									data.soon = studentJson.getString("soon");
									data.leadername = studentJson.getString("leadername");
									data.simbang_yn = studentJson.getString("simbang_yn");
									data.user_cd = studentJson.getString("user_cd");
									data.sau = studentJson.getString("sau");
									data.ban_seq = studentJson.getString("ban_seq");
									data.name = studentJson.getString("name");
									data.seq = studentJson.getString("seq");
									data.pass_day = studentJson.getString("pass_day");
									data.user_id = studentJson.getString("user_id");
									data.moimsum = studentJson.getString("moimsum");
									data.attend_yn = studentJson.getString("attend_yn");
									
									if("".equals(studentJson.getString("attend_yn")) ) {
										data.attend_yn = "N";
									}
									if("".equals(studentJson.getString("seq")) ) {
										data.seq = "";
									}
									if("".equals(studentJson.getString("ban_seq")) ) {
										data.ban_seq = "";
									}
									if("".equals(studentJson.getString("simbang_yn")) ) {
										data.simbang_yn = "N";
									}
									if("".equals(studentJson.getString("sau")) ) {
										data.sau = "";
									}
									
									mItems.add(data);
								}
							}
						}
						
						if (json.has("bogoday_cnt")) {
							String bogoDayCnt = json.getString("bogoday_cnt");
							if (bogoDayCnt.equals("1")) {
								isUpdate = true;
							} else {
								isUpdate = false;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						mItems = null;
					}
					
					// 학년 정보 화면에 표시 
					if (mItems != null) {
						mAdapter = new StudentAdapter(AttendanceActivity.this, mItems);
						mListView.setAdapter(mAdapter);
					}
				} else {
					// 에러 처리
				}
			}
		});
		
		// 학생 정보 화면에 표시 
		if (mItems != null) {
			mAdapter = new StudentAdapter(this, mItems);
			mListView.setAdapter(mAdapter);
		} else {
			// 학생정보 수신 실패 알림 
			
		}
	}
	
	/**
	 * 학생 출결 정보 add 함수 
	 * - 서버에 출결 정보를 저장한다.
	 */
	void addStudents(ArrayList<StudentData> students) {
		if (students == null) return;
		
		String jsonString = null;
		try {
			jsonString = toJSONStringForAdd(students);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (jsonString == null) return;
		
		// 출결정보 서버에 저장하기 
		HttpManager manager = HttpManager.sharedInstance();
		manager.addStudentAttendance(jsonString, new OnResultListener() {
			@Override
			public void onCompletion(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						
						if (json != null) {
							String resultOK = json.getString("result");
							
							if (resultOK.equals("ok")) {
								// 저장 성공 
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceActivity.this);
						 		alertDialogBuilder.setTitle("알림");
						 		alertDialogBuilder
						 			.setMessage("출석체크가 서버에 저장되었습니다.")
						 			.setCancelable(false)
						 			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
						 				public void onClick(DialogInterface dialog,int id) {
						 					//
						 					finish();
						 					overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_down);
						 				}
						 			 });
						 		final AlertDialog alertDialog = alertDialogBuilder.create();
						 		alertDialog.show();	
							} else { 
								// 저장 실패 
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceActivity.this);
						 		alertDialogBuilder.setTitle("알림");
						 		alertDialogBuilder
						 			.setMessage("출석체크 저장시 오류가 발생되었습니다.\n다시 시도해 주세요.")
						 			.setCancelable(false)
						 			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
						 				public void onClick(DialogInterface dialog,int id) {
						 					//
						 				}
						 			 });
						 		final AlertDialog alertDialog = alertDialogBuilder.create();
						 		alertDialog.show();	
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						// 저장 실패 
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceActivity.this);
				 		alertDialogBuilder.setTitle("알림");
				 		alertDialogBuilder
				 			.setMessage("출석체크 저장시 오류가 발생되었습니다.\n다시 시도해 주세요.")
				 			.setCancelable(false)
				 			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
				 				public void onClick(DialogInterface dialog,int id) {
				 					//
				 				}
				 			 });
				 		final AlertDialog alertDialog = alertDialogBuilder.create();
				 		alertDialog.show();	
					}
					
				} else {
					// 에러 처리
				}
			}
		});
	}
	
	/**
	 * 학생 출결 정보 update 함수 
	 * - 서버에 출결 정보를 저장한다.
	 */
	void updateStudents(ArrayList<StudentData> students) {
		if (students == null) return;
		
		String jsonString = null;
		try {
			jsonString = toJSONStringForUpdate(students);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (jsonString == null) return;
		
		// 출결정보 서버에 저장하기 
		HttpManager manager = HttpManager.sharedInstance();
		manager.updateStudentAttendance(jsonString, new OnResultListener() {
			@Override
			public void onCompletion(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						
						if (json != null) {
							String resultOK = json.getString("result");
							
							if (resultOK.equals("ok")) {
								// 저장 성공 
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceActivity.this);
						 		alertDialogBuilder.setTitle("알림");
						 		alertDialogBuilder
						 			.setMessage("출석체크가 서버에 저장되었습니다.")
						 			.setCancelable(false)
						 			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
						 				public void onClick(DialogInterface dialog,int id) {
						 					//
						 					finish();
						 					overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_down);
						 				}
						 			 });
						 		final AlertDialog alertDialog = alertDialogBuilder.create();
						 		alertDialog.show();	
							} else { 
								// 저장 실패 
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceActivity.this);
						 		alertDialogBuilder.setTitle("알림");
						 		alertDialogBuilder
						 			.setMessage("출석체크 저장시 오류가 발생되었습니다.\n다시 시도해 주세요.")
						 			.setCancelable(false)
						 			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
						 				public void onClick(DialogInterface dialog,int id) {
						 					//
						 				}
						 			 });
						 		final AlertDialog alertDialog = alertDialogBuilder.create();
						 		alertDialog.show();	
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						// 저장 실패 
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceActivity.this);
				 		alertDialogBuilder.setTitle("알림");
				 		alertDialogBuilder
				 			.setMessage("출석체크 저장시 오류가 발생되었습니다.\n다시 시도해 주세요.")
				 			.setCancelable(false)
				 			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
				 				public void onClick(DialogInterface dialog,int id) {
				 					//
				 				}
				 			 });
				 		final AlertDialog alertDialog = alertDialogBuilder.create();
				 		alertDialog.show();	
					}
					
				} else {
					// 에러 처리
				}
			}
		});
	}
	
	public String toJSONStringForAdd(ArrayList<StudentData> students) throws JSONException {
		
		StringBuffer sauBuffer = new StringBuffer();
		JSONArray jsonArray = new JSONArray();
        for (StudentData student : students) {
        	JSONObject studentJson = new JSONObject();
        	studentJson.put("birth", student.birth);
        	studentJson.put("wdiv", student.wdiv);
        	studentJson.put("pass_yn", student.pass_yn);
        	studentJson.put("commcode", student.commcode);
        	studentJson.put("dlb", student.dlb);
        	studentJson.put("leadername", student.leadername);
        	studentJson.put("simbang_yn", student.simbang_yn);
        	studentJson.put("user_cd", student.user_cd);
        	studentJson.put("sau", student.sau);
        	studentJson.put("ban_seq", student.ban_seq);
        	studentJson.put("name", student.name);
        	studentJson.put("seq", student.seq);
        	studentJson.put("pass_day", student.pass_day);
        	studentJson.put("user_id", student.user_id);
        	studentJson.put("moimsum", student.moimsum);
        	studentJson.put("attend_yn", student.attend_yn);
        	jsonArray.put(studentJson);
        	
        	sauBuffer.append(student.sau +":");
        }

        JSONObject json = new JSONObject();
		json.put("list", jsonArray);
		json.put("sau_arr", sauBuffer.substring(0, sauBuffer.length()-1));
		json.put("dlb", mClassData.grade.title);
		json.put("soon", mClassData.title);
		
        return json.toString();
    }
	
	public String toJSONStringForUpdate(ArrayList<StudentData> students) throws JSONException {
		
		StringBuffer sauBuffer = new StringBuffer();
		JSONArray jsonArray = new JSONArray();
        for (StudentData student : students) {
        	JSONObject studentJson = new JSONObject();
        	studentJson.put("birth", student.birth);
        	studentJson.put("wdiv", student.wdiv);
        	studentJson.put("pass_yn", student.pass_yn);
        	studentJson.put("commcode", student.commcode);
        	studentJson.put("dlb", student.dlb);
        	studentJson.put("leadername", student.leadername);
        	studentJson.put("simbang_yn", student.simbang_yn);
        	studentJson.put("user_cd", student.user_cd);
        	studentJson.put("sau", student.sau);
        	studentJson.put("ban_seq", student.ban_seq);
        	studentJson.put("name", student.name);
        	studentJson.put("seq", student.seq);
        	studentJson.put("pass_day", student.pass_day);
        	studentJson.put("user_id", student.user_id);
        	studentJson.put("moimsum", student.moimsum);
        	studentJson.put("attend_yn", student.attend_yn);
        	jsonArray.put(studentJson);
        	
        	sauBuffer.append(student.sau +":");
        }
        
        JSONObject json = new JSONObject();
		json.put("list", jsonArray);
		json.put("sau_arr", sauBuffer.substring(0, sauBuffer.length()-1));
		json.put("bogoday", mBogoDay);
		json.put("dlb", mClassData.grade.title);
		json.put("soon", mClassData.title);
		
        return json.toString();
    }

	public void setAbsenceCause(StudentData data) {
		// 새창을 띄우고 pos 넘겨줌 
		Intent intent = new Intent(this, SetAbsenceActivity.class);
		intent.putExtra("STUDENT", data);
		startActivityForResult(intent, REQUEST_CODE_SET_ABSENCE);
	}

}

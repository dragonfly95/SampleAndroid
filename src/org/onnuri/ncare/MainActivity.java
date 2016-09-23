package org.onnuri.ncare;

import org.onnuri.ncare.vo.CampusData;
import org.onnuri.ncare.vo.ClassData;
import org.onnuri.ncare.vo.DivisionData;
import org.onnuri.ncare.vo.GradeData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	// 액티비티 요청 코드들 
	public static final int REQUEST_CODE_CAMPUS = 11;
	public static final int REQUEST_CODE_DIVISION = 12;
	public static final int REQUEST_CODE_GRADE = 13;
	public static final int REQUEST_CODE_CLASS = 14;
	
	// 인텐트에 사용되는 키들
	public static final String CAMPUS_DATA = "CAMPUS_DATA";
	public static final String DIVISION_DATA = "DIVISION_DATA";
	public static final String GRAGE_DATA = "GRAGE_DATA";
	public static final String CLASS_DATA = "CLASS_DATA";

	ClassData mClassData = null;			// 반 
	
	TextView titleTextView;
	TextView campusNameTextView, divisionNameTextView, gradeNameTextView, classNameTextView;
	Button setCampusButton, setDivisionButton, setGradeButton, setClassButton;
	Button checkAttendanceButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 로그인에서 전달된 데이터 가져오기 
		Intent intent= getIntent();
		if (intent != null) {
			mClassData = intent.getParcelableExtra(CLASS_DATA);
		} 
		
//		// 타이틀 뷰 
//		titleTextView = (TextView) findViewById(R.id.textViewTitle);
//		titleTextView.setText("출석 체크");
		
		campusNameTextView = (TextView) findViewById(R.id.textViewCampusName);
		divisionNameTextView = (TextView) findViewById(R.id.textViewDivisionName);
		gradeNameTextView = (TextView) findViewById(R.id.textViewGradeName);
		classNameTextView = (TextView) findViewById(R.id.textViewClassName);
		
		// 캠퍼스 선택 버튼 
		setCampusButton = (Button) findViewById(R.id.buttonSetCampus);
		setCampusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 캠퍼스 선택 화면으로 이동 
				Intent intent = new Intent(MainActivity.this, CampusActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CAMPUS);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		
		// 부서 선택 버튼 
		setDivisionButton = (Button) findViewById(R.id.buttonSetDivision);
		setDivisionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mClassData != null && 
						mClassData.grade != null && 
						mClassData.grade.division != null &&
						mClassData.grade.division.campus != null && 
						mClassData.grade.division.campus.title != null) {
					// 부서 선택 화면으로 이동 
					Intent intent = new Intent(MainActivity.this, DivisionActivity.class);
					intent.putExtra(MainActivity.CAMPUS_DATA, mClassData.grade.division.campus);
					startActivityForResult(intent, REQUEST_CODE_DIVISION);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				} else {
					// 캠퍼스를 선택해 주세요 
					setViewWithData();
				}
				
			}
		});
		
		// 학년 선택 버튼 
		setGradeButton = (Button) findViewById(R.id.buttonSetGrade);
		setGradeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mClassData != null && 
						mClassData.grade != null && 
						mClassData.grade.division != null &&
						mClassData.grade.division.title != null) {
					// 학년 선택 화면으로 이동 
					Intent intent = new Intent(MainActivity.this, GradeActivity.class);
					intent.putExtra(MainActivity.DIVISION_DATA, mClassData.grade.division);
					startActivityForResult(intent, REQUEST_CODE_GRADE);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				} else {
					// 부서를 선택해 주세요 
					setViewWithData();
				}
			}
		});
		
		// 반 선택 버튼 
		setClassButton = (Button) findViewById(R.id.buttonSetClass);
		setClassButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mClassData != null && 
						mClassData.grade != null &&
						mClassData.grade.title != null) {
					// 반 선택 화면으로 이동 
					Intent intent = new Intent(MainActivity.this, ClassActivity.class);
					intent.putExtra(MainActivity.GRAGE_DATA, mClassData.grade);
					startActivityForResult(intent, REQUEST_CODE_CLASS);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				} else {
					// 학년을 선택해 주세요 
					setViewWithData();
				}
			}
		});
		
		// 출석체크 버튼 
		checkAttendanceButton = (Button) findViewById(R.id.buttonCheckAttendance);
		checkAttendanceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mClassData != null &&
						mClassData.code != null) {
					// 출석체크 화면으로 이동 
					Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
					intent.putExtra(MainActivity.CLASS_DATA, mClassData);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_top);
				} else {
					setViewWithData(); 
				}
			}
		});
		
		// 화면 초기화 
		setViewWithData();
	}

	/**
	 * 화면 표시 함수 
	 */
	void setViewWithData() {
		if (mClassData == null) {
			campusNameTextView.setText("캠퍼스를 선택해 주세요");
			divisionNameTextView.setText("부서를 선택해 주세요");
			gradeNameTextView.setText("학년을 선택해 주세요");
			classNameTextView.setText("반을 선택해 주세요");
		} else {
			if (mClassData.title != null) {
				classNameTextView.setText(String.valueOf(mClassData.title));
			} else {
				setClassButton.setVisibility(View.VISIBLE);
				classNameTextView.setText("반을 선택해 주세요");
			}
			
			if (mClassData.grade == null) {
				campusNameTextView.setText("캠퍼스를 선택해 주세요");
				divisionNameTextView.setText("부서를 선택해 주세요");
				gradeNameTextView.setText("학년을 선택해 주세요");
			} else {
				if (mClassData.grade.title != null) {
					gradeNameTextView.setText(String.valueOf(mClassData.grade.title));
				} else {
					setGradeButton.setVisibility(View.VISIBLE);
					gradeNameTextView.setText("학년을 선택해 주세요");
				}
				
				if (mClassData.grade.division == null) {
					campusNameTextView.setText("캠퍼스를 선택해 주세요");
					divisionNameTextView.setText("부서를 선택해 주세요");
				} else {
					if (mClassData.grade.division.title != null) {
						setDivisionButton.setVisibility(View.INVISIBLE);
						divisionNameTextView.setText(String.valueOf(mClassData.grade.division.title));
					} else {
						setDivisionButton.setVisibility(View.VISIBLE);
						divisionNameTextView.setText("부서를 선택해 주세요");
					}
					
					if (mClassData.grade.division.campus == null) {
						setCampusButton.setVisibility(View.VISIBLE);
						campusNameTextView.setText("캠퍼스를 선택해 주세요");
					} else {
						if (mClassData.grade.division.campus.title != null) {
							setCampusButton.setVisibility(View.INVISIBLE);
							campusNameTextView.setText(String.valueOf(mClassData.grade.division.campus.title));
						} else {
							setCampusButton.setVisibility(View.VISIBLE);
							campusNameTextView.setText("캠퍼스를 선택해 주세요");
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_CAMPUS) {
			if (resultCode == Activity.RESULT_OK) {
				CampusData campus = data.getParcelableExtra(CAMPUS_DATA);
				
				if (campus != null) {
					mClassData = new ClassData();
					mClassData.grade.division.campus = campus;
					
					setViewWithData();
				}
			}
		} else if (requestCode == REQUEST_CODE_DIVISION) {
			if (resultCode == Activity.RESULT_OK) {
				DivisionData division = data.getParcelableExtra(DIVISION_DATA);	
				
				if (division != null) {
					mClassData = new ClassData();
					mClassData.grade.division = division;
					
					setViewWithData();
				}
			}
		} else if (requestCode == REQUEST_CODE_GRADE) {
			if (resultCode == Activity.RESULT_OK) {
				GradeData grade = data.getParcelableExtra(GRAGE_DATA);
				
				if (grade != null) {
					mClassData = new ClassData();
					mClassData.grade = grade;
					
					setViewWithData();
				}
			}
		} else if (requestCode == REQUEST_CODE_CLASS) {
			if (resultCode == Activity.RESULT_OK) {
				mClassData = data.getParcelableExtra(CLASS_DATA);
				
				if (mClassData != null) {
					mClassData = mClassData;
					
					setViewWithData();
				}
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
		
}

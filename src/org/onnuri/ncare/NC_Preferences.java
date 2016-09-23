package org.onnuri.ncare;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.content.SharedPreferences;

public class NC_Preferences {
	private static final String TAG = NC_Preferences.class.getSimpleName();

	private static final String NC_PREFERENCES = "NC_Preferences";
	private static final String NC_SERVER = "http://icare.ionnuri.org";
	private static final String NC_PREFIX = "child_ecare/rest/comm";
	private static final String NC_METHOD_GET_CAMPUS = "getKk";
	private static final String NC_METHOD_GET = "get";
	private static final String NC_METHOD_GET_CLASS_MEMBER = "commarrg";

	private volatile static NC_Preferences instance = null;
	private static Context mContext;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mPreferencesEditor;
	
	// 쿠키 정보 
	public static Cookie cookie;
	
	// 사용자 이름
	public static String name;
	
	// GCM 등록번호 
	public static String deviceNumber;
	
	public static String dateFormatString = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat travelDateFormat = new SimpleDateFormat(dateFormatString, Locale.KOREA);
	public static String showDateFormatString = "yyyy-MM-dd HH:mm:";


	public NC_Preferences(Context context) {
		mContext = context;
		
		mPreferences = mContext.getSharedPreferences(NC_PREFERENCES, Context.MODE_PRIVATE);
		mPreferencesEditor = mPreferences.edit();
		
	}
	
	public static NC_Preferences sharedInstance(Context context) {
		if(instance == null) {
			synchronized (NC_Preferences.class) {
				if (instance == null) {
					instance = new NC_Preferences(context);
				}
			}
		}
		
		return instance;
	}
	
}

package org.onnuri.ncare.webservice;

import org.apache.http.cookie.Cookie;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


public class HttpManager {
	private static final String TAG = HttpManager.class.getSimpleName();
	
	
	private static final String URL = "icare.ionnuri.org";
	private static final String PORT = null;
	private static final String PREFIX = "/child_ecare/rest";
	private String SERVER_URL = null;
	
	private volatile static HttpManager instance = null; 
	private HttpClient mHttpClient = null;
	
	
	public HttpManager() {		
		if (mHttpClient == null) {
			mHttpClient = new HttpClient();
		}
		if (PORT == null) {
			SERVER_URL = "http://" + URL + PREFIX;
		} else {
			SERVER_URL = "http://" + URL + ":" + PORT + PREFIX;
		}
		
	}
	
	// 싱글톤 패턴 적용
	public static HttpManager sharedInstance() {
		if(instance == null) {
			synchronized (HttpManager.class) {
				if(instance == null) {
					instance = new HttpManager();
				}
			}
		}
		
		return instance;
	}
	
	public Cookie getCookie() {
		return mHttpClient.getCookie();
	}
	
//	/**
//	 * JSON 반환 함수 
//	 * - 주어진 Bundle 정보를 이용해 JSON 데이터를 생성하여 반환한다. 
//	 * 
//	 * @param params Bundle
//	 * @return JSON Data
//	 * @throws JSONException
//	 */
//	private String getJsonParams(Bundle params) throws JSONException {
//		JSONObject json = new JSONObject();
//	
//		if (params != null) {
//			 for (String key : params.keySet()) {
//				 Object value = params.get(key);
//				 if (value instanceof ArrayList) {
//					 JSONArray array = new JSONArray();
//	
//					 ArrayList temps = params.getParcelableArrayList(key);
//					 if (temps.size() > 0)
//						 if (temps.get(0) instanceof String) {
//							 ArrayList<String> list = params.getStringArrayList(key);
//							 for (String val : list) {
//								 array.put(val);
//							 }
//						 }
//						 else if (temps.get(0) instanceof Bundle) {
//							 ArrayList<Bundle> list = params.getParcelableArrayList(key);
//							 for (Bundle bundle : list) {
//								 Set<String> keys = bundle.keySet();
//								 JSONObject _json = new JSONObject();
//								 for (String _key : keys) {
//									 _json.put(_key, bundle.get(_key));
//								 }
//								 array.put(_json);
//							 }
//						 }
//					 json.put(key, array);
//				 }
//				 else if (value instanceof String[]) {
//					 JSONArray array = new JSONArray();
//					 String[] list = params.getStringArray(key);
//					 for (String val : list) {
//						 array.put(val);
//					 }
//					 
//					 json.put(key, array);
//				 }
//				 else
//					 json.put(key, value);
//			}
//		}
//		
//		return json.toString();
//	}

	/**
	 * 로그인 처리 함수 
	 * @param id
	 * @param password
	 * @param listener
	 */
	public void login(String id, String password, OnResultListener listener) {
		String args[] = {id, password};
		
		new LoginTask(listener).execute(args);
	}
	
	private class LoginTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public LoginTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/login";
			
			Bundle args = new Bundle();
			args.putString("userid", params[0]);
			args.putString("passwd", params[1]);
			
			String response = mHttpClient.postJsonRequest(SERVER_URL, METHOD, args);
//			Log.i(TAG, "LoginTask response " + response);
						
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);			
	    }
	}
	
	/**
	 * 로그아웃 처리 함수 
	 * @param url
	 * @param port
	 * @param prefix
	 * @param listener
	 */
	public void logout(OnResultListener listener) {
		new LogoutTask(listener).execute();
	}
	
	private class LogoutTask extends AsyncTask<Void, Void, String> {
		OnResultListener listener;
		
		public LogoutTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String METHOD = "/user/logout";
			
			String response = mHttpClient.postRequest(SERVER_URL, METHOD, null);
//			Log.i(TAG, "LogoutTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}
	
	/**
	 * 캠퍼스 정보 가져오기 함수 
	 * 
	 * @param listener
	 */
	public void getCampus(OnResultListener listener) {
		new CampusLoadTask(listener).execute();
	}
	
	private class CampusLoadTask extends AsyncTask<Void, Void, String> {
		OnResultListener listener;
		
		public CampusLoadTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String METHOD = "/comm/getKk";
			
			String response = mHttpClient.getRequest(SERVER_URL + METHOD);
			Log.i(TAG, "CampusLoadTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}
	
	/**
	 * 부서 정보 가져오기 함수 
	 * - 주어진 캠퍼스의 부서 정보를 가져온다. 
	 * 
	 * @param campus
	 * @param listener
	 */
	public void getDivision(String campus, OnResultListener listener) {
		String args[] = {campus};
		
		new DivisionLoadTask(listener).execute(args);
	}
	
	private class DivisionLoadTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public DivisionLoadTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/comm/get" + "/" + params[0];
			
			String response = mHttpClient.getRequest(SERVER_URL + METHOD);
			Log.i(TAG, "DivisionLoadTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}
	
	/**
	 * 학년 정보 가져오기 함수 
	 * - 주어진 캠퍼스의 부서에 대한 학년 정보를 가져온다. 
	 * 
	 * @param campus
	 * @param division
	 * @param listener
	 */
	public void getGrade(String campus, String division, OnResultListener listener) {
		String args[] = {campus, division};
		
		new GradeLoadTask(listener).execute(args);
	}
	
	private class GradeLoadTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public GradeLoadTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/comm/get" + "/" + params[0] + "/" + params[1];
			
			String response = mHttpClient.getRequest(SERVER_URL + METHOD);
			Log.i(TAG, "GradeLoadTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}

	/**
	 * 반 정보 가져오기 함수 
	 * - 주어진 캠퍼스의 부서의 학년에 대한 반 정보를 가져온다. 
	 * 
	 * @param campus
	 * @param division
	 * @param grade
	 * @param listener
	 */
	public void getClass(String campus, String division, String grade, OnResultListener listener) {
		String args[] = {campus, division, grade};
		
		new ClassLoadTask(listener).execute(args);
	}
	
	private class ClassLoadTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public ClassLoadTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/comm/get/" + params[0] + "/" + params[1] + "/" + params[2];
			
			String response = mHttpClient.getRequest(SERVER_URL + METHOD);
			Log.i(TAG, "ClassLoadTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}
	
	/**
	 * 학생 정보 가져오기 함수
	 * - 주어진 반 코드에 대한 학생 정보를 가져온다. 
	 * 
	 * @param classCode
	 * @param listener
	 */
	public void getStudent(String classCode, OnResultListener listener) {
		String args[] = {classCode};
		
		new StudentLoadTask(listener).execute(args);
	}
	
	private class StudentLoadTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public StudentLoadTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/banact/list" + "/" + params[0];
			
			String response = mHttpClient.getRequest(SERVER_URL + METHOD);
			Log.i(TAG, "StudentLoadTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}

	
	/**
	 * 출석체크 저장 함수 
	 * 
	 * @param jsonString
	 * @param listener
	 */
	public void saveStudentAttendance(String jsonString, OnResultListener listener) {
		String args[] = {jsonString};
		
		new SaveStudentAttendanceTask(listener).execute(args);
	}
	
	private class SaveStudentAttendanceTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public SaveStudentAttendanceTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/banact/save";
			
			String response = mHttpClient.postJsonRequest(SERVER_URL, METHOD, params[0]);
			Log.i(TAG, "SaveStudentAttendanceTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}

	/**
	 * 출석체크 저장 함수 
	 * 
	 * @param jsonString
	 * @param listener
	 */
	public void addStudentAttendance(String jsonString, OnResultListener listener) {
		String args[] = {jsonString};
		
		new AddStudentAttendanceTask(listener).execute(args);
	}
	
	private class AddStudentAttendanceTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public AddStudentAttendanceTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/banact/choolsuk_add";
			
			String response = mHttpClient.postJsonRequest(SERVER_URL, METHOD, params[0]);
			Log.i(TAG, "AddStudentAttendanceTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}
	
	
	/**
	 * 출석체크 수정 함수 
	 * 
	 * @param jsonString
	 * @param listener
	 */
	public void updateStudentAttendance(String jsonString, OnResultListener listener) {
		String args[] = {jsonString};
		
		new UpdateStudentAttendanceTask(listener).execute(args);
	}
	
	private class UpdateStudentAttendanceTask extends AsyncTask<String, Void, String> {
		OnResultListener listener;
		
		public UpdateStudentAttendanceTask(OnResultListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String METHOD = "/banact/choolsuk_update";
			
			String response = mHttpClient.postJsonRequest(SERVER_URL, METHOD, params[0]);
			Log.i(TAG, "UpdateStudentAttendanceTask response " + response);
			
			if (response != null) {
				response = response.trim();
			}
			
			return response;
	    }
	
		@Override
	    protected void onPostExecute(String result) {	
			listener.onCompletion(result);
	    }
	}
	
	
	
}

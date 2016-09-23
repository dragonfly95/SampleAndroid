package org.onnuri.ncare.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onnuri.ncare.NC_Preferences;

import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class HttpClient {
	private static final String TAG = HttpClient.class.getSimpleName();
	
	private AndroidHttpClient mHttpClient = null;
	private HttpContext mHttpContext = null;
	private CookieStore mCookieStore = null;
	
	public HttpClient() {
		mHttpClient = AndroidHttpClient.newInstance(TAG);
		HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), 5000);
		HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), 5000);
		
		mHttpContext = new BasicHttpContext();
		
		mCookieStore = new BasicCookieStore();
		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
		
		mHttpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
		mHttpContext.setAttribute(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	}

	public void closeConnections() {
		mHttpClient.getConnectionManager().closeExpiredConnections();
//		mHttpClient.getConnectionManager().closeIdleConnections(idletime, tunit)
	}

	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		
		mHttpClient.close();
		mHttpClient.getConnectionManager().shutdown();
		mHttpContext = null;
		mCookieStore = null;
		mHttpClient = null;
	}
	
	/**
	 * 쿠키 리스트 반환 함수 
	 * @return
	 */
	public List<Cookie> getCookies() {		
		return mCookieStore.getCookies();
	}
	
	/**
	 * 쿠키 반환 함수 
	 * @return
	 */
	public Cookie getCookie() {
		List<Cookie> cookies = getCookies();
				
		if (cookies == null || cookies.size() == 0) {
			Log.i(TAG, "getCookie null");
			return null;
		}
		
//		for(Cookie c : cookies) {
//			Log.i(TAG, "getCookie"
//					+ "Name " + c.getName() + "\n"
//					+ "Value" + c.getValue() + "\n"
//					+ "Domain " + c.getDomain() + "\n"
//					+ "ExpiryDate " + c.getExpiryDate());
//		}
		
		return cookies.get(0);
	}
	
	/**
	 * 쿠키 업데이트 함수 
	 */
	private void updateCookie() {	
		if (getCookie() == null && NC_Preferences.cookie != null) {
			
			// 저장된 쿠키를 http client 추가 
			mCookieStore.addCookie(NC_Preferences.cookie);
		}
	}

	/**
	 * uri 반환 함수 <br>
	 * @param url		// 서버 주소
	 * @param method	// 메소드명
	 * @param params	// 파라메터 
	 * @return
	 */
	private String getUriString(String url, String method, String params) {
		String uriString = null;
		
		if (params != null) {
			uriString = url + method + "?" + params;
		} else {
			uriString = url + method;
		}
		
		return uriString;
	}
	
	/**
	 * 파라메터 인코딩 함수
	 * @param parameters
	 * @return : uri 포멧으로 인코딩된 String
	 */
	private String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) first = false; 
            else sb.append('&');
            
            sb.append(URLEncoder.encode(key) + "=" +
                      URLEncoder.encode(parameters.getString(key)));
        }
        
        return sb.toString();
    }

	/**
	 * URL 디코딩 함수
	 * uri 포멧의 String을 Bundl 형태로 변환하여 반환하는 함수 
	 * @param s
	 * @return : Bundle
	 */
	private Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                params.putString(URLDecoder.decode(v[0]),
                                 URLDecoder.decode(v[1]));
            }
        }
        
        return params;
    }

	/**
	 * List<NameValuePair> 반환 함수 <br>
	 * - 주어진 Bundle 데이터를 List<NameValuePair>로 변환하여 반환한다.
	 * @param params
	 * @return : List<NameValuePair>
	 */
	private List<NameValuePair> getListParamsForBundle(Bundle params) {
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
	
		if (params != null) {
			 for (String key : params.keySet()) {
				formParams.add(new BasicNameValuePair(key, params.getString(key)));
			}
		}
		
		return formParams;
	}

	/**
	 * MultipartEntity 반환 함수 <br>
	 * - 주어진 String 배열을 MultipartEntity로 변환하여 반환한다.
	 * @param paths
	 * @return
	 */
	private MultipartEntity getMultipartEntity(String[] paths) {
		MultipartEntity entity = new MultipartEntity();
		
		MimeTypeMap mtm	= MimeTypeMap.getSingleton();
		for (int i=0; i<paths.length; i++) {
			File file		= new File(paths[i]);
			String fileName	= file.getName();
			String mimeType	= mtm.getMimeTypeFromExtension(fileName.substring(fileName.lastIndexOf(".")+1));
			FileBody body	= new FileBody(file, mimeType);
			entity.addPart("file" + i, body);
		}
		
		return entity;
	}

	/**
	 * JSON 데이터 반환 함수 <br>
	 * - 주어진 Bundle 데이터를 JSON 포멧의 데이터로 변환하여 반환한다.
	 * @param params
	 * @return
	 * @throws JSONException
	 */
	private String getJsonParams(Bundle params) throws JSONException {
		JSONObject json = new JSONObject();
	
		if (params != null) {
			 for (String key : params.keySet()) {
				 
				 Object value = params.get(key);
				 if (value instanceof ArrayList) {
					 JSONArray array = new JSONArray();
	
					 ArrayList temps = params.getParcelableArrayList(key);
					 if (temps.size() > 0)
						 if (temps.get(0) instanceof String) {
							 ArrayList<String> list = params.getStringArrayList(key);
							 for (String val : list) {
								 array.put(val);
							 }
						 } else if (temps.get(0) instanceof Bundle) {
							 ArrayList<Bundle> list = params.getParcelableArrayList(key);
							 for (Bundle bundle : list) {
								 Set<String> keys = bundle.keySet();
								 JSONObject _json = new JSONObject();
								 for (String _key : keys) {
									 _json.put(_key, bundle.get(_key));
								 }
								 array.put(_json);
							 }
						 }
					 json.put(key, array);
				 } else if (value instanceof String[]) {
					 JSONArray array = new JSONArray();
					 String[] list = params.getStringArray(key);
					 for (String val : list) {
						 array.put(val);
					 }
					 
					 json.put(key, array);
				 } else {
					 json.put(key, value);
				 }
			}
		}
		
		return json.toString();
	}

	/**
	 * getRequest 요청 처리 함수
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public String getRequest(String url, String method, String params) {
		return getRequest(getUriString(url, method, params));
	}

	/**
	 * getRequest 요청 처리 함수
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public String getRequest(String url, String method, Bundle params) {
		return getRequest(getUriString(url, method, encodeUrl(params)));
	}
	
	/**
	 * getRequest 요청 처리 함수
	 * @param uriString
	 * @return
	 */
	public String getRequest(String uriString) {
		Log.d(TAG, "getRequest " + uriString);

		String responseString = null;		
		
		try {
			HttpGet httpGetRequest = new HttpGet(uriString);
			updateCookie();
			HttpResponse response = mHttpClient.execute(httpGetRequest, mHttpContext);
			HttpEntity responseEntity = response.getEntity();
			responseString = EntityUtils.toString(responseEntity, "UTF-8");
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		getCookie();
		return responseString;
	}
	
	public String putRequest(String url, String method, String params) {
		return putRequest(getUriString(url, method, params));
	}
	
	public String putRequest(String url, String method, Bundle params) {
		String jsonString = null;
		try {
			jsonString = getJsonParams(params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return putRequest(url + method + " '" + jsonString + "'");
	}
	
	/**
	 * putRequest 요청 처리 함수 
	 * @param uriString
	 * @return
	 */
	public String putRequest(String uriString) {
		Log.d(TAG, "putRequest " + uriString);

		String responseString = null;

		try {
			HttpPut httpPutRequest = new HttpPut(uriString);

			updateCookie();
			HttpResponse response = mHttpClient.execute(httpPutRequest, mHttpContext);
			HttpEntity responseEntity = response.getEntity();
			responseString = EntityUtils.toString(responseEntity, "UTF-8");
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
		getCookie();
		return responseString;
	}
	
	public String putJsonRequest(String url, String method, Bundle params) {
		try {
			String jsonParams = getJsonParams(params);
			Log.i(TAG, "jsonParams " + jsonParams);
			String replaceJsonParams = jsonParams.replace("\\","");
			Log.i(TAG, "replaceJsonParams " + replaceJsonParams);

			return putJsonRequest(url, method, replaceJsonParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public String putJsonRequest(String url, String method, String params) {		
		String responseString = null;
		
		String uriString = url + method;
		Log.d(TAG, "putJsonRequest " + uriString + " params " + params);
		try {
			HttpPut httpPutRequest = new HttpPut(uriString);
	    	StringEntity entity = new StringEntity(params, HTTP.UTF_8);
			entity.setContentType("application/json");
			httpPutRequest.setEntity(entity);
			HttpResponse response = mHttpClient.execute(httpPutRequest, mHttpContext);
			HttpEntity responseEntity = response.getEntity();
			responseString = EntityUtils.toString(responseEntity, "UTF-8");
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
    	return responseString;
	}
	
	/**
	 * postRequest 요청 처리 함수
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
//	public String postRequest(String url, String method, Bundle params) {
//		
//		return postRequest(url, method, getListParamsForBundle(params));
//	}
	
	/**
	 * postRequest 요청 처리 함수
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public String postRequest(String url, String method, List<NameValuePair> params) {
		String responseString = null;
		
		String uriString = url + method;
		if (params != null) {
			Log.d(TAG, "postRequest " + uriString + " params " + params);
		}
		
    	try {
        	HttpPost httpPostRequest = new HttpPost(uriString);

    		if (params != null) {
				UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		    	httpPostRequest.setEntity(requestEntity);
    		}
    		updateCookie();
			HttpResponse response = mHttpClient.execute(httpPostRequest, mHttpContext);
			HttpEntity responseEntity = response.getEntity();
			responseString = EntityUtils.toString(responseEntity, "UTF-8");
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
    	getCookie();
    	return responseString;
	}
	
	/**
	 * MultiPartForm postRequest 요청 처리 함수
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public String postMultiPartFormRequest(String url, String method, Bundle params) {		
		String responseString = null;
		
		String uriString = url + method;
		Log.d(TAG, "postMultiPartFormRequest " + uriString + " params " + params);
		
    	try {
    		HttpPost httpPostRequest = new HttpPost(uriString);
    		StringBuilder stringBuilder = new StringBuilder();
    		httpPostRequest.setHeader("ENCTYPE", "multipart/form-data");
			MultipartEntity requestEntity = getMultipartEntity(params.getStringArray("image_path"));
			httpPostRequest.setEntity(requestEntity);
			HttpResponse response = mHttpClient.execute(httpPostRequest, mHttpContext);
			HttpEntity responseEntity = response.getEntity();
						
			if (response.getStatusLine().getStatusCode() == 200) { 
				BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent())); 
				String line; 
				while ((line = reader.readLine()) != null) { 
					stringBuilder.append(line).append("\n"); 
				} 
			} else { 
				// handle "error loading data" 
			}
			
			responseString = stringBuilder.toString();
			responseEntity.consumeContent();
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
    	return responseString;
	}
	
	/**
	 * Json postRequest 요청 처리 함수
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public String postJsonRequest(String url, String method, Bundle params) {
		try {
			String jsonParams = getJsonParams(params);
			return postJsonRequest(url, method, jsonParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Json postRequest 요청 처리 함수
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public String postJsonRequest(String url, String method, String params) {		
		String responseString = null;
		
		String uriString = url + method;
		Log.d(TAG, "postJsonRequest " + uriString + " params " + params);
		try {
	    	HttpPost httpPostRequest = new HttpPost(uriString);
	    	StringEntity entity = new StringEntity(params, HTTP.UTF_8);
			entity.setContentType("application/json");
			httpPostRequest.setEntity(entity);
			HttpResponse response = mHttpClient.execute(httpPostRequest, mHttpContext);
			HttpEntity responseEntity = response.getEntity();
			responseString = EntityUtils.toString(responseEntity, "UTF-8");
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
    	return responseString;
	}
	
	public HttpEntity getHttpEntityForGetRequest(String url) {
		HttpEntity entity = null;
		
		Log.d(TAG, "getRequest " + url);

    	try {
    		HttpGet httpGet = new HttpGet(url);
			HttpResponse response = mHttpClient.execute(httpGet, mHttpContext);
			entity = response.getEntity();
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	
		return entity;
	}
	
	public HttpEntity getHttpEntityForPostRequest(String url, String method, List<NameValuePair> params) {
		HttpEntity entity = null;
		
		Log.d(TAG, "postRequest " + url + " params " + params);

    	try {
        	HttpPost httpPost = new HttpPost(url);
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	    	httpPost.setEntity(ent);
			HttpResponse response = mHttpClient.execute(httpPost, mHttpContext);
			entity = response.getEntity();
		} catch (UnsupportedEncodingException e)  {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	
		return entity;
	}
	
}

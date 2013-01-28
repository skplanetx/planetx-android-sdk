package com.skp.openplatform.android.sdk.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/***
 * Http 통신이후 인증서버로부터 받아온 OAuth 정보를 Android 내부의 SharedPreferences 를 이용하여<br/>
 * 저장 / 복구 하는 기능을 수행하는 클래스
 * 
 * @author lhjung
 * 
 */
public class OAuthInfoManager {

	private final static String TAG = "OAuthInfoManager";
	
	public static Context context;
	
	public static AuthorInfo authorInfo = new AuthorInfo();

	static SharedPreferences sp;
	static SharedPreferences.Editor spe;
	
	
	// OAuth 인증을 통하기 위한. 요구되는 3가지 정보.
	public static String clientId 				= "";
	public static String clientSecret 			= "";
	public static String scope 				= "";
	public static String code 					= "";		// 서버로부터 받는값
	
	public static final String response_type 	= "code";
	public static final String redirect_uri 	= "http://localhost/";		// 빈값.
	public static String grant_type 	= "authorization_code";
	
	public static String error = "";
	public static String error_desc = "";
	
	public OAuthInfoManager() {
		if(authorInfo == null)
		{
			authorInfo = new AuthorInfo();
		}		
	}
	
	public OAuthInfoManager(Context context) {
		if(authorInfo == null)
		{
			authorInfo = new AuthorInfo();
		}
		OAuthInfoManager.context = context;
	}

	public void setContext(Context context)
	{
		OAuthInfoManager.context = context;
	}
	
	private static void initSharedPreferences() throws PlanetXOAuthException {
		if(context == null)
		{
			throw new PlanetXOAuthException("ERR_CD_00007", Constants.ERR_CD_00007);
		}
		
		if (sp == null) {
			sp = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		}
		if (spe == null) {
			spe = sp.edit();
		}
	}

	/***
	 * OAuth Info를 Andorid - SharedPreferences 를 이용해서 저장
	 * @throws PlanetXOAuthException
	 */
	public static void saveOAuthInfo() throws PlanetXOAuthException {
		initSharedPreferences();

		spe.putString(Constants.HEADER_ACCESS_TOKEN, authorInfo.getAccessToken());
		spe.putString(Constants.HEADER_REFRESH_TOKEN, authorInfo.getRefreshToken());
		spe.putString(Constants.OAUTH_END_EXPIRES_IN, authorInfo.getExpires_in());
		spe.putString(Constants.OAUTH_EXPIRES_SYSTEME, authorInfo.getExpires_systime());

		spe.commit();
	}

	/***
	 * 저장된 OAuth Info를 Andorid - SharedPreferences 를 이용해서 복원
	 * @throws PlanetXOAuthException
	 */
	public static void restoreOAuthInfo() throws PlanetXOAuthException {
		log("restoreOAuthInfo()" );
		
		initSharedPreferences();
		if (authorInfo == null) {
			authorInfo = new AuthorInfo();
		}

		authorInfo.setAccessToken(sp.getString(Constants.HEADER_ACCESS_TOKEN, ""));
		authorInfo.setRefreshToken(sp.getString(Constants.HEADER_REFRESH_TOKEN, ""));
		authorInfo.setExpires_in(sp.getString(Constants.OAUTH_END_EXPIRES_IN,""));
		authorInfo.setExpires_systime(sp.getString(Constants.OAUTH_EXPIRES_SYSTEME,""));
		
		
	}

	public void setOAuthInfo(AuthorInfo oai) {
		this.authorInfo = oai;
	}

	public AuthorInfo getAuthroInfo() throws PlanetXOAuthException {
		initSharedPreferences();
		return authorInfo;
	}

	public Map<String, String> getOAuthInfoMap() throws PlanetXOAuthException {
		initSharedPreferences();

		Map<String, String> returnMap = new HashMap<String, String>();

		returnMap.put(Constants.HEADER_ACCESS_TOKEN, sp.getString(Constants.HEADER_ACCESS_TOKEN, ""));
		returnMap.put(Constants.HEADER_REFRESH_TOKEN, sp.getString(Constants.HEADER_REFRESH_TOKEN, ""));

		return returnMap;
	}

	
	/***
	 * 토큰 재 발행.
	 * @return
	 * @throws PlanetXOAuthException 
	 * @throws IOException 
	 */
	public static boolean reissueAccessToken() throws PlanetXOAuthException, IOException
	{
		boolean result = false;
		
		String response = "";

		URL url = new URL(getAccessTokenReissueUrl());

		
		HttpURLConnection http = null;

		if (url.getProtocol().toLowerCase().equals("https")) {
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			http = https;
		} else {
			http = (HttpURLConnection) url.openConnection();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null){
			response += inputLine;
		}
		in.close();
	
		log("Rcv Data : " + response);
		
		if(response != null && response.indexOf("access_token") > -1) {
			// Set New Token Info
			int oauthResult = setOAuthInfo(response);
			if(oauthResult != -1)
			{
				result = true;	
			}
		}
		return result;
		
	}
	
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate
		// chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getAccessTokenReissueUrl()
	{
		StringBuffer sb = new StringBuffer("");
		sb.append(Constants.Url.OAUTH_ACCESS);
		try {
			sb.append("?" + Constants.OAUTH_CLIENT_ID 		+ "=" + URLEncoder.encode(OAuthInfoManager.clientId, "UTF-8"));
			sb.append("&" + Constants.OAUTH_CLIENT_SECRET 	+ "=" + URLEncoder.encode(OAuthInfoManager.clientSecret, "UTF-8"));
			sb.append("&" + Constants.OAUTH_REDIRECT_URI 	+ "=" + URLEncoder.encode(OAuthInfoManager.redirect_uri, "UTF-8"));
			sb.append("&" + Constants.OAUTH_GRANT_TYPE 		+ "=refresh_token");
			sb.append("&" + Constants.OAUTH_SCOPE 			+ "=" + URLEncoder.encode(OAuthInfoManager.scope, "UTF-8"));
			sb.append("&" + Constants.OAUTH_REFRESH_TOKEN 	+ "=" + URLEncoder.encode(authorInfo.getRefreshToken(), "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		log("OPEN URL[REFTKN] : " + sb.toString());
		
		return sb.toString();
	}
	
	
	private static String getRevokeTokenUrl()
	{
		String sb = "";
		sb += Constants.Url.OAUTH_REVOKE;
		try {
			sb += "?"; 
			sb += Constants.OAUTH_CLIENT_ID;
			sb += "=";
			sb += URLEncoder.encode(OAuthInfoManager.clientId, "UTF-8");
			sb += "&"; 
			sb += Constants.OAUTH_TOKEN;
			sb += "=";
			sb += URLEncoder.encode(OAuthInfoManager.authorInfo.getAccessToken(), "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		log("OPEN URL[RVKTKN] : " + sb);
		
		return sb;
	}
	
	
	private static void log(String log){
		if(Constants.IS_DEBUG) {
			System.out.println(log);
		}

	}
	
	
	/***
	 * URL 호출 결과 수신한 OAuth Info (Access Token, Refresh Token, Expires_In , Scope 정보)를 OAuthInfoManger의 oAuth Info 에 설정한다.
	 * @param jsonOauthInfo OAuth End Point 호출 결과
	 */
	public static int setOAuthInfo(String jsonOauthInfo)
	{
		if(Constants.IS_DEBUG)
		{
			Log.d(TAG, "setOAuthInfo() -> RESULT \n" + jsonOauthInfo);
		}
		JSONObject jsonObject;

		try{
			jsonObject = new JSONObject(jsonOauthInfo);
			if(!jsonObject.isNull("error"))
			{
//				String errorMsg = jsonObject.getString("error") + "\n" + jsonObject.getString("error_description");
//				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				return -1;
			}
		}
		catch(JSONException e)
		{
			e. printStackTrace();
		}
		
		
		try {
			jsonObject = new JSONObject(jsonOauthInfo);
			if(!jsonObject.isNull(Constants.OAUTH_END_ACCESS_TOKEN)){
				String tmpAccessToken = jsonObject.getString(Constants.OAUTH_END_ACCESS_TOKEN);
				OAuthInfoManager.authorInfo.setAccessToken(tmpAccessToken);
			}
			
			if(!jsonObject.isNull(Constants.OAUTH_END_REFRESH_TOKEN)){
				String tmpRefreshToken = jsonObject.getString(Constants.OAUTH_END_REFRESH_TOKEN);
				OAuthInfoManager.authorInfo.setRefreshToken(tmpRefreshToken);
			}
			
			if(!jsonObject.isNull(Constants.OAUTH_END_EXPIRES_IN)){
				String tmpExpresIn = jsonObject.getString(Constants.OAUTH_END_EXPIRES_IN);
				OAuthInfoManager.authorInfo.setExpires_in(tmpExpresIn);
			}
			
			if(!jsonObject.isNull(Constants.OAUTH_END_SCOPE)){
				String tmpScope = jsonObject.getString(Constants.OAUTH_END_SCOPE);
				OAuthInfoManager.authorInfo.setScope(tmpScope);
			}
			
			// ExpiresIn 시간을 기준으로 Expires system 을 설정한다.
			Long extTime = Long.decode(OAuthInfoManager.authorInfo.getExpires_in());
			log("ExtTime : " + extTime);
			Long expSTime = System.currentTimeMillis() + (extTime*1000);
			OAuthInfoManager.authorInfo.setExpires_systime(expSTime + "");
			
			
			if(Constants.IS_DEBUG)
			{
				Log.d(TAG, "============OAUTH INFO START=========" );
				Log.d(TAG, "\n" + OAuthInfoManager.authorInfo.getWholeInfo());
				Log.d(TAG, "============OAUTH INFO END  =========" );
			}
			
			// save OAuthinfo to storage
			OAuthInfoManager.saveOAuthInfo();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PlanetXOAuthException e) {
			e.printStackTrace();
		}
		
		
		return 1;
		
		
	}
	

	private static Handler oAuthHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				if(msg.obj != null){
					new OAuthClient(context, (OAuthListener) msg.obj).show();
				}
				else
				{
					new OAuthClient(context).show();
				}
			} catch (PlanetXOAuthException e) {
				e.printStackTrace();
			}
		};
	};
	
	/***
	 * Access Token의 유효성을 확인하여<br/>
	 * Access Token을 그대로 사용할지<br/>
	 * Refresh Token을 이용하여 토큰을 갱신(reissue)할지 <br/>
	 * Login Dialog를 호출할지를 결정한다.
	 * 
	 * @throws PlanetXOAuthException
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void login(final Context context, final OAuthListener oAuthListener) throws PlanetXOAuthException {

		OAuthInfoManager.context = context;
		
		log("login()");

		// 저장된 정보 복구
		OAuthInfoManager.restoreOAuthInfo();

		// 저장되어 있는 Access Token이 Null 이거나 공백이 아니면.
		if (OAuthInfoManager.authorInfo.getAccessToken() != null && !"".equals(OAuthInfoManager.authorInfo.getAccessToken())) {
			new LoginAsyncTask().execute(oAuthListener);
		}
		else
		{
			// 저장되어 있는 값이 널이거나 없는 값이면 로그인 창을 띄운다.
			android.os.Message msg = new Message();
			msg.obj = oAuthListener;
			oAuthHandler.sendMessage(msg);
		}

	}
	
	private static class LoginAsyncTask extends AsyncTask<Object, String, Boolean> {

		private OAuthListener oAuthListener = null; 
		
		@Override
		protected Boolean doInBackground(Object... params) {
			if(params.length > 0 && (params[0] instanceof OAuthListener))
			{
				oAuthListener = (OAuthListener)params[0];	
			}
			
			boolean res = false;
			try {
				res = OAuthInfoManager.reissueAccessToken();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (PlanetXOAuthException e) {
				e.printStackTrace();
			}
			
			return res;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				// 로그인 Dialog를 띄운다.
				log("Token Case 2 : Refresh Token is invalid");
				
				android.os.Message msg = new Message();
				if(this.oAuthListener != null){
					msg.obj = this.oAuthListener;
				}
				
				oAuthHandler.sendMessage(msg);
					
					
			} else {
				// Access Token 갱신에 성공.
				log("Token Case 3 : to Success to Refresh Access Token");
				if(this.oAuthListener != null) {
					this.oAuthListener.onComplete("success");
				}
			}
			
		}
	
	}

	private static class LogoutAsyncTask extends AsyncTask<Object, String, Boolean> {
		private OAuthListener oAuthListener = null; 
		
		@Override
		protected Boolean doInBackground(Object... params) {
			if(params.length > 0 && (params[0] instanceof OAuthListener))
			{
				oAuthListener = (OAuthListener)params[0];	
			}
			
			boolean res = true;

			try {
				log("try to logout");
				res = OAuthInfoManager.revokeTokenToServer();
			} catch (Exception e) {
				res = false;
			}
			return res;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				log("Logout Success");
				OAuthInfoManager.authorInfo.setAccessToken("");
				OAuthInfoManager.authorInfo.setRefreshToken("");
				OAuthInfoManager.authorInfo.setExpires_systime("");
				OAuthInfoManager.authorInfo.setExpires_in("");
				
				try {
					OAuthInfoManager.saveOAuthInfo();
				} catch (PlanetXOAuthException e) {
					e.printStackTrace();
				}
				
				if (oAuthListener != null) {
					oAuthListener.onComplete("logout success");
				}
			} else {
				log("Logout failed");
				if (oAuthListener != null) {
					oAuthListener.onError("logout failed");
				}
			}
			
		}
	}
	
	
	
	
	public static void login(Context context) throws PlanetXOAuthException {
		login(context, null);
	}

	
	public static void logout(final Context context, final OAuthListener oAuthListener) {
		OAuthInfoManager.context = context;
		new LogoutAsyncTask().execute(oAuthListener);
	}
	
	/***
	 * AT RT 파기.
	 * @return
	 * @throws PlanetXOAuthException 
	 */
	private static boolean revokeTokenToServer() throws IOException, PlanetXOAuthException
	{
		boolean result = false;
		
		String response = "";

		URL url = new URL(getRevokeTokenUrl());

		HttpURLConnection http = null;

		if (url.getProtocol().toLowerCase().equals("https")) {
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			http = https;
		} else {
			http = (HttpURLConnection) url.openConnection();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null){
			response += inputLine;
		}
		in.close();
	
		log("Rcv Data : " + response);
		
		//{"app":{"result":"success"}}
		if(response != null && response.indexOf("success") > -1) {
			result = true;
		}
		return result;
		
	}
	
	
	/***
	 * Access Token 을 저장하고 있는 ExpiresSystime과 비교하여 
	 * 유효한 시간 값인지 확인한다.
	 * @return
	 */
	public static boolean isValidAccessToken() {
		boolean isValid = false;

		String strLimit = "";
		
		if(OAuthInfoManager.authorInfo.getExpires_systime() == null || "".equals(OAuthInfoManager.authorInfo.getExpires_systime())){
			log("OAuthInfoManager.authorInfo.getExpires_systime() is NULL!!!!");
			return isValid;
		}
		else {
			strLimit = OAuthInfoManager.authorInfo.getExpires_systime();
		}
		
		// Min Sec mSec 10분을 의미.
		Long bufferTime = 10l * 60l * 1000l; 
		Long limitTime = Long.valueOf(strLimit) - bufferTime;
		Long currentTime = System.currentTimeMillis();

		if(limitTime > currentTime){
			isValid = true;
		}

		log("Buf Time : " + bufferTime);
		log("LMT Time : " + milliToString(limitTime) + " / " + limitTime);
		log("CUR Time : " + milliToString(currentTime) + " / " + currentTime);
		log("IS Valid? : " + isValid);
		
		
		return isValid;		
	}
	
	private static String milliToString(Long milli) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return df.format(milli)+"";
	}
	
	
}

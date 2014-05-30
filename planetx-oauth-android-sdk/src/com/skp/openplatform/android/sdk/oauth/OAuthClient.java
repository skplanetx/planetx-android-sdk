package com.skp.openplatform.android.sdk.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/***
 * OAuth 인증처리를 위한 Dialog
 * @author lhjung
 *
 */
public class OAuthClient extends Dialog {

	final String TAG = "OAuthClient";
	
	private OAuthListener oAuthListener;
	
	private static String ENCODE_UTF_8 = "UTF-8";
	
	public OAuthClient(Context context) throws PlanetXOAuthException {
		super(context);
		// 타이틀바 제거
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		OAuthInfoManager.context = context;
		this.oAuthListener = null;
		initUI();
	}

	
	@Override
	public void onPanelClosed(int featureId, Menu menu) {
		super.onPanelClosed(featureId, menu);
	}
	
	public OAuthClient(Context context, OAuthListener oAuthListener) throws PlanetXOAuthException {
		super(context);
		// 타이틀바 제거
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		OAuthInfoManager.context = context;
		this.oAuthListener = oAuthListener;
		initUI();
	}

	// Outline
	private RelativeLayout rlOutline;
	
	// Inner Content
	private WebView webView;

	private LayoutParams lp;
	
	@SuppressLint("SetJavaScriptEnabled")
	public void initUI()
	{
		lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		rlOutline = new RelativeLayout(getContext());
		rlOutline.setLayoutParams(lp);
		
		webView = new WebView(getContext());
		webView.setLayoutParams(lp);
		
		clearCookies();		
		
		webView.getSettings().setPluginState(PluginState.ON);
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setSupportMultipleWindows(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setWebChromeClient(new OAuthWebChromeClient());
		webView.setWebViewClient(new OAuthWebViewClient());
		
		webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString()+" " + "oauth/1.0");
		
		
		webView.loadUrl(getOAuthorizationUrl());
		
		rlOutline.addView(webView);
		
		setContentView(rlOutline);
		
		// resizing by keypad
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	private String getOAuthorizationUrl()
	{
		StringBuffer sb = new StringBuffer("");
		sb.append(Constants.Url.OAUTH_AUTHEN);
		try {

			sb.append("?" + Constants.OAUTH_CLIENT_ID 		+ "=" + URLEncoder.encode(OAuthInfoManager.clientId, ENCODE_UTF_8));
			sb.append("&" + Constants.OAUTH_RESPONSE_TYPE 	+ "=" + URLEncoder.encode(OAuthInfoManager.response_type, ENCODE_UTF_8));
			sb.append("&" + Constants.OAUTH_SCOPE 			+ "=" + URLEncoder.encode(OAuthInfoManager.scope, ENCODE_UTF_8));
			sb.append("&" + Constants.OAUTH_REDIRECT_URI 	+ "=" + URLEncoder.encode(OAuthInfoManager.redirect_uri, ENCODE_UTF_8));
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(Constants.IS_DEBUG){
			System.out.println("OPEN URL[AUTH] : " + sb.toString());
		}
		return sb.toString();
	}
	
	private String getOAccessTokenUrl()
	{
		StringBuffer sb = new StringBuffer("");
		sb.append(Constants.Url.OAUTH_ACCESS);
		try {
			sb.append("?" + Constants.OAUTH_CLIENT_ID 		+ "=" + URLEncoder.encode(OAuthInfoManager.clientId, ENCODE_UTF_8));
			sb.append("&" + Constants.OAUTH_SCOPE 			+ "=" + URLEncoder.encode(OAuthInfoManager.scope, ENCODE_UTF_8));
			sb.append("&" + Constants.OAUTH_REDIRECT_URI 	+ "=" + URLEncoder.encode(OAuthInfoManager.redirect_uri, ENCODE_UTF_8));
	
			sb.append("&" + Constants.OAUTH_CLIENT_SECRET 	+ "=" + URLEncoder.encode(OAuthInfoManager.clientSecret, ENCODE_UTF_8));
			sb.append("&" + Constants.OAUTH_CODE 			+ "=" + URLEncoder.encode(OAuthInfoManager.code, ENCODE_UTF_8));
			sb.append("&" + Constants.OAUTH_GRANT_TYPE 		+ "=" + URLEncoder.encode(OAuthInfoManager.grant_type, ENCODE_UTF_8));
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		 
		if(Constants.IS_DEBUG){
			System.out.println("OPEN URL[ACTK] : " + sb.toString());
		}
		return sb.toString();
	}
	
	private class OAuthWebChromeClient extends WebChromeClient {

		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			new AlertDialog.Builder(view.getContext()).setTitle("SK Planet OAuth").setMessage(message)
					.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).setCancelable(true).create().show();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			new AlertDialog.Builder(view.getContext()).setTitle("SK Planet OAuth").setMessage(message)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					}).create().show();
			return true;
		}
		
		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog,
				boolean isUserGesture, Message resultMsg) {

			// for upper then kitkat
			WebView.HitTestResult result = view.getHitTestResult();
			String url = result.getExtra();
			log("onCreateWindow URL : " + url);

			if (url != null && url.indexOf("___target=_blank") > -1) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				OAuthInfoManager.context.startActivity(i);
				return true;
			}
			
			return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
		}
	}	
	
	public void clearCookies()
	{
		log("clearCookies()");
		if(OAuthInfoManager.context != null){
			log("clearCookies() - Success");
			CookieSyncManager.createInstance(OAuthInfoManager.context);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		}
		log("//clearCookies()");
	}
	
	private class OAuthWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			log("shouldOverrideUrlLoading URL : " + url);
			
			if(url.indexOf("___target=_blank") > -1){
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				OAuthInfoManager.context.startActivity(i);
				return true;
			}
			
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			
			log(TAG+"errorCode : " + errorCode);
			log(TAG+"description : " + description);
			log(TAG+"failingUrl : " + failingUrl);
			
			if(oAuthListener != null){
				oAuthListener.onError(errorCode+"");
			}
			dismiss();
			
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			log(TAG+ " onPageFinished " + " URL : " + url);

			if (url.indexOf("error_description") > -1 && url.indexOf("error") > -1) { // 에러 상황일때
				OAuthInfoManager.error = StringUtil.getValueFromQueryString(url, "error");
				OAuthInfoManager.error_desc = StringUtil.getValueFromQueryString(url, "error_description");
				
				String errorMessage = OAuthInfoManager.error_desc;
				 try {
					errorMessage=URLDecoder.decode(errorMessage,ENCODE_UTF_8);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				if(oAuthListener != null) {
					oAuthListener.onError(OAuthInfoManager.error + OAuthInfoManager.error_desc);
				}
				
				dismiss();
			}
			else if (url.indexOf("access_complete_mobile") > -1 ) { // 처리 완료 페이지 일 때
				
				OAuthInfoManager.code = StringUtil.getValueFromQueryString(url, "code");
	
				new ReadUrl().execute(getOAccessTokenUrl());
				dismiss();

			}
			
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			log(TAG + "onReceived-Ssl--Error()");
			handler.proceed();
		}

	    private class ReadUrl extends AsyncTask<String, String, String>{

			@Override
			protected String doInBackground(String... param) {
				// URL 응답 값 가져오기
				String response = "";
				try {
					URL url = new URL(param[0]);
					
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
				
					while ((inputLine = in.readLine()) != null)
						response += inputLine;
					in.close();
			        
				} catch (MalformedURLException e) {
					e.printStackTrace();
					cancel(true);
				} catch (IOException e) {
					e.printStackTrace();
					cancel(true);;
				}
				return response;
			}
			
			@Override
			protected void onPostExecute(String result) {
				if(result != null){
					// set Received OAuth Infomation to memory
					int oAuthResult = OAuthInfoManager.setOAuthInfo(result);
					dismiss();
					
					if(oAuthListener != null) {
						if(oAuthResult != -1)
						{
							oAuthListener.onComplete("");
						}
						else {
							oAuthListener.onError("Failed to parsing OAuth Information");
						}
					}
					return;
				}
				else
				{
					System.out.println("Result is null or isLogOn is false");
					if(oAuthListener != null) {
						oAuthListener.onError(OAuthInfoManager.error + OAuthInfoManager.error_desc);
					}
				}
				
				super.onPostExecute(result);
			}
					
			@Override
			protected void onProgressUpdate(String... values) {
				super.onProgressUpdate(values);
			}
 
			final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			private void trustAllHosts() {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return new java.security.cert.X509Certificate[] {};
					}

					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}
				} };

				try {
					SSLContext sc = SSLContext.getInstance("TLS");
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
	    } // End ReadUrl
		
	}
	
	public void log(String log) {
		if(Constants.IS_DEBUG){
//			System.out.println(log);
			Log.d(this.getClass().getName(), "LOG : " + log);
		}
	}
	
}

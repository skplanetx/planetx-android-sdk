package com.skp.openplatform.android.sdk.oauth;


public class Constants {
	
	public static boolean IS_DEBUG = false;
	
	public static final String SHARED_PREF_NAME = "SKPOpenPlatform_SharedPreferences_00";

	public static final String HEADER_ACCESS_TOKEN 	= "access_token";
	public static final String HEADER_REFRESH_TOKEN 	= "refresh_token";
	
	// OAUTH ONLY : STEP 1 AUTHORIZATION
	public static final String OAUTH_CLIENT_ID 		= "client_id";
	public static final String OAUTH_RESPONSE_TYPE	= "response_type";	// VALUE : code
	public static final String OAUTH_SCOPE 			= "scope";
	public static final String OAUTH_REDIRECT_URI		= "redirect_uri";	// VALUE : 
	// OAUTH ONLY : STEP 2 ACCESSTOKEN	
	public static final String OAUTH_CLIENT_SECRET	= "client_secret";
	public static final String OAUTH_CODE 			= "code";			// 인증 Endpoint에서 받은 Authorization code
	public static final String OAUTH_GRANT_TYPE		= "grant_type";		// VALUE : authorization_code

	// OAUTH ONLY : REFRESH TOKEN
	public static final String OAUTH_REFRESH_TOKEN 	= "refresh_token";
	
	// OAUTH ONLY : ROVOKE TOKEN
	public static final String OAUTH_TOKEN 	= "token";

	
	public static final String OAUTH_END_ACCESS_TOKEN 	= "access_token";
	public static final String OAUTH_END_REFRESH_TOKEN 	= "refresh_token";
	public static final String OAUTH_END_EXPIRES_IN 		= "expires_in";
	public static final String OAUTH_END_SCOPE 			= "scope";
	
	public static final String OAUTH_EXPIRES_SYSTEME		= "expires_systime";
	
	
	
	public static class Url {
		public static String OAUTH_SERVER = "https://oneid.skplanetx.com";
		// OAuth STEP1 : 인증 Endpoint
		public static String OAUTH_AUTHEN = OAUTH_SERVER + "/oauth/authorize";
		// OAuth STEP2(1+2) : Access Token 발급 Endpoint
		public static String OAUTH_ACCESS = OAUTH_SERVER + "/oauth/token";
		// OAuth 토큰 폐기
		public static String OAUTH_REVOKE = OAUTH_SERVER + "/oauth/expireToken";
	}
	
	/***
	 * ERROR_CODE 
	 */
	public static final String ERR_CD_00003 = "ReturnType-value is wrong";
		
	public static final String ERR_CD_00006 = "Application Key is null or empty";	
	public static final String ERR_CD_00007 = "Context is null.";	
	
	
}

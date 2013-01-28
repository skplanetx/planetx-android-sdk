package com.skp.openplatform.android.sdk.oauth;


/***
 * Preferences에 저장되는 정보를 관리.
 * OAuth 와 SKPOP Android SDK 의 분리를 위해 OAuthInfomanger에서 관리하던 OAuthinfo를 가져옴. 
 * @author lhjung
 *
 */
public class AuthorInfo {
//	public static String appKey = "";
	public static String accessToken = "";
	public static String refreshToken = "";
	public static String expires_in = "";
	public static String scope = "";

	private static String expires_systime = "";

	public String getExpires_systime() {
		return expires_systime;
	}

	public void setExpires_systime(String expires_systime) {
		AuthorInfo.expires_systime = expires_systime;
	}

	public AuthorInfo() {
		super();
//		AuthorInfo.appKey = "";
		AuthorInfo.accessToken = "";
		AuthorInfo.refreshToken = "";
		AuthorInfo.expires_in = "";
		AuthorInfo.scope = "";
		AuthorInfo.expires_systime = "";
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		AuthorInfo.expires_in = expires_in;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		AuthorInfo.scope = scope;
	}

//	public String getAppKey() {
//		return appKey;
//	}
//
//	public void setAppKey(String appKey) {
//		AuthorInfo.appKey = appKey;
//	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		AuthorInfo.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		AuthorInfo.refreshToken = refreshToken;
	}

	public String getWholeInfo() {
		StringBuffer sb = new StringBuffer("");
//		sb.append("appKey:" + appKey + "\n");
		sb.append("accessToken:" + accessToken + "\n");
		sb.append("refreshToken:" + refreshToken + "\n");
		sb.append("expires_in:" + expires_in + "\n");
		sb.append("scope:" + scope + "\n");
		sb.append("expire_systime:" + expires_systime + "\n");

		return sb.toString();

	}
}

package com.skp.openplatform.android.sdk.oauth;

public interface OAuthListener {
	public void onComplete(String completeInfo);
	public void onError(String errorMessage);

}

package com.skplanet.openplatform.testproject;

import android.content.Intent;
import android.os.Bundle;

import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.BaseActivity;
import com.skp.openplatform.android.sdk.oauth.OAuthInfoManager;

public class Init extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initOAuthData();
		moveToMainPage();
	}

	public void initOAuthData() {

		
		// Input here.
		APIRequest.setAppKey("##APPKEY_INPUTHERE##");
		
		OAuthInfoManager.clientId = "##CLIENTID_INPUTHERE##";
		OAuthInfoManager.clientSecret = "##CLIENTSECRET_INPUTHERE##";
		OAuthInfoManager.scope = "##SCOPE_INPUTHERE##";
		 
		
	}
	
	public void moveToMainPage() {
		startActivity(new Intent(this, Main.class));
		finish();
	}

}

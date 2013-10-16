package com.skplanet.openplatform.testproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.skp.openplatform.android.sdk.common.BaseActivity;
import com.skp.openplatform.android.sdk.oauth.OAuthInfoManager;
import com.skp.openplatform.android.sdk.oauth.OAuthListener;
import com.skp.openplatform.android.sdk.oauth.PlanetXOAuthException;

public class Main extends BaseActivity implements OnClickListener {

	Button btnOAuth;

	// Button List
	Button btnMenu0;
	Button btnMenu1;
	Button btnMenu2;
	Button btnMenu3;
	Button btnMenu4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		initUI();
		allocListener();

	}

	public void initUI() {
		btnOAuth = (Button) findViewById(R.id.MAIN_BTN_LOGIN);
		btnMenu0 = (Button) findViewById(R.id.MAIN_BTN_MELON_NEWSONGS);
		btnMenu1 = (Button) findViewById(R.id.MAIN_BTN_ELEVENSTREET_CATEGORIES);
		btnMenu2 = (Button) findViewById(R.id.MAIN_BTN_TCLOUD_IMAGES);
		btnMenu3 = (Button) findViewById(R.id.MAIN_BTN_TCLOUD_IMAGES_DELETE);
		btnMenu4 = (Button) findViewById(R.id.MAIN_BTN_TCLOUD_IMAGE_TAGS);
	}

	public void allocListener() {

		Button btns[] = {
			btnOAuth,
			btnMenu0,
			btnMenu1,
			btnMenu2,
			btnMenu3,
			btnMenu4			
		};
		
		for(Button btn  : btns)
		{
			btn.setOnClickListener(this);
		}
		
	}

	OAuthListener oauthlis = new OAuthListener() {
		
		@Override
		public void onError(String errorMessage) {
			System.out.println("onError : " + errorMessage);
		}
		
		@Override
		public void onComplete(String message) {
			System.out.println("onComplete : " + message);
		}
	};
	
	@Override
	public void onClick(View v) {

		int id = v.getId();

		switch (id) {
		case R.id.MAIN_BTN_LOGIN: {
			try {
				OAuthInfoManager.login(this, oauthlis);
			} catch (PlanetXOAuthException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.MAIN_BTN_MELON_NEWSONGS: {
			startActivity(new Intent(this, MelonNewSongs.class));
			break;
		}
		case R.id.MAIN_BTN_ELEVENSTREET_CATEGORIES: {
			startActivity(new Intent(this, ElevenStreetCategories.class));
			break;
		}
		case R.id.MAIN_BTN_TCLOUD_IMAGES: {
			startActivity(new Intent(this, TcloudImages.class));
			break;
		}
		case R.id.MAIN_BTN_TCLOUD_IMAGES_DELETE: {
			startActivity(new Intent(this, TcloudImagesDelete.class));
			break;
		}
		case R.id.MAIN_BTN_TCLOUD_IMAGE_TAGS: {
			startActivity(new Intent(this, TcloudImageTags.class));
			break;
		}
		
		default:
			break;
		}
	}

}

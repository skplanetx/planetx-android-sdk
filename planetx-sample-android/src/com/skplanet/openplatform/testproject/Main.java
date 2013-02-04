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
	Button btnMelonNewSongs;
	Button btnNateOnGetProfile;
	Button btnNateOnModProfile;
	Button btnCLogWriteArticle;
	Button btnCLogUploadImage;
	Button btnCLogDeleteArticle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		initUI();
		allocListener();

	}

	public void initUI() {
		btnOAuth = (Button) findViewById(R.id.MAIN_BTN_LOGIN);
		btnMelonNewSongs = (Button) findViewById(R.id.MAIN_BTN_MELON_NEWSONGS);
		btnNateOnGetProfile = (Button) findViewById(R.id.MAIN_BTN_NATEON_GETPROFILE);
		btnNateOnModProfile = (Button) findViewById(R.id.MAIN_BTN_NATEON_MODPROFILE);
		btnCLogWriteArticle = (Button) findViewById(R.id.MAIN_BTN_CLOG_WRITE_ARTICLE);
		btnCLogUploadImage = (Button) findViewById(R.id.MAIN_BTN_CLOG_IMG_UPLOAD);
		btnCLogDeleteArticle = (Button) findViewById(R.id.MAIN_BTN_CLOG_DELETE_ARTICLE);
	}

	public void allocListener() {

		Button btns[] = {
			btnOAuth,
			btnMelonNewSongs,
			btnNateOnGetProfile,
			btnNateOnModProfile,
			btnCLogWriteArticle,
			btnCLogUploadImage,
			btnCLogDeleteArticle
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
		case R.id.MAIN_BTN_NATEON_GETPROFILE: {
			startActivity(new Intent(this, NateOnGetProfile.class));
			break;
		}
		case R.id.MAIN_BTN_NATEON_MODPROFILE: {
			startActivity(new Intent(this, NateOnModProfile.class));
			break;
		}
		case R.id.MAIN_BTN_CLOG_WRITE_ARTICLE: {
			startActivity(new Intent(this, CyworldNoteWriteArticle.class));
			break;
		}
		case R.id.MAIN_BTN_CLOG_IMG_UPLOAD: {
			startActivity(new Intent(this, CyworldNoteUploadFile.class));
			break;
		}
		case R.id.MAIN_BTN_CLOG_DELETE_ARTICLE: {
			startActivity(new Intent(this, CyworldNoteDeleteArticle.class));
			break;
		}
		
		default:
			break;
		}
	}

}

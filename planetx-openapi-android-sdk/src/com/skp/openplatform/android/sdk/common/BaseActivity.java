package com.skp.openplatform.android.sdk.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.skp.openplatform.android.sdk.util.Logger;

public class BaseActivity extends Activity {

	private String TAG = this.getClass().getName();
	public static Resources res;
	private Context ctx;
	protected Logger l;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		l.d("onCreate");

	}

	public void init() {
		if (l == null) {
			l = new Logger(TAG);
		}
		if (ctx == null) {
			ctx = getApplicationContext();
		}
		if (res == null) {
			res = getResources();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		l.d("onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		l.d("onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		l.d("onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		l.d("onDestroy");
	}
	
	

}

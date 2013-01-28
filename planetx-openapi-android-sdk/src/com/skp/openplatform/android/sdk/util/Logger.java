package com.skp.openplatform.android.sdk.util;

import android.util.Log;

import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants;


public class Logger {

	private String tag = "";

	public Logger(String tag) {
		super();
		this.tag = tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void d(String str) {
		d(tag, str);
	}

	public void e(String str) {
		e(tag, str);
	}

	public void w(String str) {
		w(tag, str);
	}

	public void i(String str) {
		i(tag, str);
	}

	public void v(String str) {
		v(tag, str);
	}

	public void d(String TAG, String str) {
		if (PlanetXSDKConstants.IS_DEBUG) {
			Log.d(TAG, str);
		}
	}

	public void e(String TAG, String str) {
		if (PlanetXSDKConstants.IS_DEBUG) {
			Log.e(TAG, str);
		}
	}

	public void w(String TAG, String str) {
		if (PlanetXSDKConstants.IS_DEBUG) {
			Log.w(TAG, str);
		}
	}

	public void i(String TAG, String str) {
		if (PlanetXSDKConstants.IS_DEBUG) {
			Log.i(TAG, str);
		}
	}

	public void v(String TAG, String str) {
		if (PlanetXSDKConstants.IS_DEBUG) {
			Log.v(TAG, str);
		}
	}

}

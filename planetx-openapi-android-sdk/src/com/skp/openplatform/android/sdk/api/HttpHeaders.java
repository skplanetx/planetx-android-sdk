package com.skp.openplatform.android.sdk.api;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

	
	Map<String, String> headerMap;
	
	
	public HttpHeaders() {
		super();
		checkInit();
	}
	
	private void checkInit(){
		if(headerMap == null)
		{
			headerMap = new HashMap<String, String>();
		}
	}

	public void put(String key, String value) {
		checkInit();
		headerMap.put(key, value);
	}
	
	public String get(String key)
	{
		return headerMap.get(key);
	}

	public Map<String, String> getHeader()
	{
		checkInit();
		return headerMap;
	}
	
}

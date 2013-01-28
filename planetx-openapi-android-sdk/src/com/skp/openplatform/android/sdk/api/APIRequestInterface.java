package com.skp.openplatform.android.sdk.api;

import java.io.IOException;
import java.net.MalformedURLException;

import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.HttpMethod;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.RequestListener;
import com.skp.openplatform.android.sdk.common.ResponseMessage;

/***
 * 
 * @author lhjung
 * @since 2012.05.25
 * 
 */
interface APIRequestInterface {

	public ResponseMessage request(RequestBundle bundle) throws PlanetXSDKException;

	public ResponseMessage request(RequestBundle bundle, HttpMethod httpMethod) throws PlanetXSDKException;
	
	public ResponseMessage request(RequestBundle bundle, String url, HttpMethod httpMethod) throws PlanetXSDKException;

	public void request(RequestBundle bundle, RequestListener requestListener) throws PlanetXSDKException ;
	public void request(RequestBundle bundle, HttpMethod httpMethod, RequestListener requestListener) throws PlanetXSDKException ;
	public void request(RequestBundle bundle, String url, HttpMethod httpMethod, RequestListener requestListener) throws PlanetXSDKException ;

	
}

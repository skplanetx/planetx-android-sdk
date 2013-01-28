package com.skp.openplatform.android.sdk.common;

import java.io.IOException;

/***
 * 비동기 호출에 대한 응답케이스
 * 
 * @author lhjung
 * 
 */
public interface RequestListener {

	public void onComplete(ResponseMessage response);

	public void onPlanetSDKException(PlanetXSDKException e);

}

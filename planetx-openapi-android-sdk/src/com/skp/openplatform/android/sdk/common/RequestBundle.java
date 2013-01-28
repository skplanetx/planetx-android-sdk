package com.skp.openplatform.android.sdk.common;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.CONTENT_TYPE;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.HttpMethod;

public class RequestBundle {

	/***
	 * Request시에 기본으로 실리는 데이터들.<br>
	 * app id 및 oauth 정보가 실리게된다.
	 */
	private static Map<String, Object> header;

	/***
	 * OPEN API URL
	 */
	private String url;

	/***
	 * PathParam , QueryString, FormEncoded(Key, Value)
	 */
	private Map<String, Object> parameters;

	/***
	 * ContentBody부분에 삽입되는 데이터(XML JSON FORM ~), RequestType과 일치해야함.
	 */
	private String payload;

	/***
	 * File Upload시에 사용
	 */
	private File uploadFile;

	/***
	 * File Upload Key에 사용
	 */
	private String uploadFileKey;

	/***
	 * GET PUT POST DELETE
	 */
	private HttpMethod httpMethod;

	/***
	 * Request시에 Payload에 실리는 Content Type
	 */
	private CONTENT_TYPE requestType;

	/***
	 * Response Content Type (Accept)
	 */
	private CONTENT_TYPE responseType;

	/***
	 * 비동기 통신에 사용하는 리스너
	 */
	private RequestListener requestListener;

	public void setAppID(String appId) {
		if (header == null) {
			header = new HashMap<String, Object>();
		}

		header.put("appId", appId);
	}

	public RequestBundle() {
		super();
		this.url = "";
		this.parameters = new LinkedHashMap<String, Object>();
		this.payload = "";
		this.uploadFile = null;
		this.httpMethod = HttpMethod.GET;
		this.requestType = CONTENT_TYPE.JSON;
		this.responseType = CONTENT_TYPE.JSON;
		this.requestListener = null;
	}

	public RequestBundle(String url, Map<String, Object> parameters, String payload, File uploadFile, HttpMethod httpMethod,
			CONTENT_TYPE requestType, CONTENT_TYPE responseType, RequestListener asyncRequestListener) {
		super();
		this.url = url;
		this.parameters = parameters;
		this.payload = payload;
		this.uploadFile = uploadFile;
		this.httpMethod = httpMethod;
		this.requestType = requestType;
		this.responseType = responseType;
		this.requestListener = asyncRequestListener;
	}

	/****************
	 ****************/

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getParameters() {
		if(parameters == null) {
			parameters = new LinkedHashMap<String, Object>();
		}
			
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	/***
	 * Using setUploadFile(String fileKey, File uploadFile)
	 * 
	 * @param uploadFile
	 */
	@Deprecated
	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public void setUploadFile(String fileKey, File uploadFile) {
		this.uploadFileKey = fileKey;
		this.uploadFile = uploadFile;
	}

	public String getUploadFileKey() {
		if("".equals(uploadFileKey))
		{
			return "image";
		}
		return uploadFileKey;
	}

	public void setUploadFileKey(String uploadFileKey) {
		this.uploadFileKey = uploadFileKey;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public CONTENT_TYPE getRequestType() {
		return requestType;
	}

	public void setRequestType(CONTENT_TYPE requestType) {
		this.requestType = requestType;
	}

	public CONTENT_TYPE getResponseType() {
		return responseType;
	}

	public void setResponseType(CONTENT_TYPE responseType) {
		this.responseType = responseType;
	}

	public RequestListener getRequestListener() {
		return requestListener;
	}

	public void setRequestListener(RequestListener requestListener) {
		this.requestListener = requestListener;
	}

	public static Map<String, Object> getHeader() {
		return header;
	}

	public static void setHeader(Map<String, Object> header) {
		RequestBundle.header = header;
	}

}

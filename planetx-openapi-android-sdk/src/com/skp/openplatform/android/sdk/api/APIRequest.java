package com.skp.openplatform.android.sdk.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.webkit.WebSettings;

import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.CONTENT_TYPE;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.HttpMethod;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.RequestListener;
import com.skp.openplatform.android.sdk.common.ResponseMessage;

/***
 * HttpConnection 구현부.<br>
 * PAYLOAD 가 실리는 부는 Http Method 기본 통신으로 POST 설정.<br>
 * URL 및 QS, PP 만 사용되서 호출하는 부는 기본으로 GET으로 호출.- 물론 설정으로 변경가능함.<br>
 * 
 * @author lhjung
 * @since 2012.05.25
 * 
 */
public class APIRequest implements APIRequestInterface {

	private static HttpHeaders httpHeader = null;
	
	private static String appKey = "";

	public APIRequest() {
		super();
	}
	
	public static String getAppKey() {
		return appKey;
	}
	
	public static void setAppKey(String appKey) {
		APIRequest.appKey = appKey;
	}

	private void setHeader() throws PlanetXSDKException{
		if (httpHeader == null) {
			httpHeader = new HttpHeaders();
		}

		if(APIRequest.appKey == null ) {
			throw new PlanetXSDKException("ERR_CD_00005", PlanetXSDKConstants.ERR_CD_00005);
		}

		boolean existAuthorInfo = true;
		Class cls = null;
		try {
			cls = Class.forName("com.skp.openplatform.android.sdk.oauth.AuthorInfo");
			log(cls.toString());
		} catch (ClassNotFoundException e1) {
			existAuthorInfo = false;
		}
		
		httpHeader.put(PlanetXSDKConstants.HEADER_APP_KEY, APIRequest.appKey);
		
		// Public Case를 위하여 Try Catch로 감쌈.
		if(existAuthorInfo){
			String at = "";
			String rt = "";
			
			try {
				at = cls.getField("accessToken").get(cls).toString();
				rt = cls.getField("refreshToken").get(cls).toString();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			log("Access Token : " + at);
			log("Refresh Token : " + rt);
			
			httpHeader.put(PlanetXSDKConstants.HEADER_ACCESS_TOKEN, at);
			httpHeader.put(PlanetXSDKConstants.HEADER_REFRESH_TOKEN, rt);
		}

	}	
	
	/***
	 * POST Case
	 * 
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestPost(RequestBundle requestBundle) {

		HttpPost httpPost = new HttpPost(urlConverter(requestBundle));

		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(requestBundle.getPayload(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Parameter가 Null 이 아니면.
		if (stringEntity != null) {
			log("[[Fat Body]]");
			httpPost.setEntity(stringEntity);
		} else {
			log("[[Null body.]]");
		}

		return httpPost;
	}
	
	/***
	 * POST Case ( Form )
	 * 
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestPostForm(RequestBundle requestBundle) {

		HttpPost httpPost = new HttpPost(urlConverter(requestBundle));

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		Map<String, Object> map = requestBundle.getParameters();
		Set<Entry<String, Object>> mapSet = map.entrySet();

		for (Entry<String, Object> mapSetItem : mapSet) {
			if (mapSetItem.getValue() instanceof String) {
				params.add(new BasicNameValuePair(mapSetItem.getKey(), (String) mapSetItem.getValue()));
			}
		}

		UrlEncodedFormEntity urlEncodedFormEntity = null;
		try {
			urlEncodedFormEntity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Parameter가 Null 이 아니면.
		if (urlEncodedFormEntity != null) {
			log("[[Fat Body]]");
			httpPost.setEntity(urlEncodedFormEntity);
		} else {
			log("[[Null body.]]");
		}
		return httpPost;
	}
	
	/***
	 * POST Case ( File )
	 * 
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestPostFile(RequestBundle requestBundle) {
		HttpPost httpPost = new HttpPost(urlConverter(requestBundle));

//		httpHeader.put("Content-Type", "multipart/form-data");
		
		httpHeader.headerMap.remove("Content-Type");
		
		MultipartEntity multiPartEntity = new MultipartEntity();
		FileBody fileBody = new FileBody(requestBundle.getUploadFile(), "multipart/form-data");
		multiPartEntity.addPart(requestBundle.getUploadFileKey(), fileBody);

		httpPost.setEntity(multiPartEntity);
		
		return httpPost;
	}
	
	/***
	 * PUT Case
	 * 
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestPut(RequestBundle requestBundle) {
		HttpPut httpPut = new HttpPut(urlConverter(requestBundle));

		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(requestBundle.getPayload(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Parameter가 Null 이 아니면.
		if (stringEntity != null) {
			log("[[Fat Body]]");
			httpPut.setEntity(stringEntity);
		} else {
			log("[[Null body.]]");
		}

		return httpPut;
	}
	
	/***
	 * PUT Case ( Form )
	 * 
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestPutForm(RequestBundle requestBundle) {
		HttpPut httpPut = new HttpPut(urlConverter(requestBundle));

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		Map<String, Object> map = requestBundle.getParameters();
		Set<Entry<String, Object>> mapSet = map.entrySet();

		for (Entry<String, Object> mapSetItem : mapSet) {
			if (mapSetItem.getValue() instanceof String) {
				params.add(new BasicNameValuePair(mapSetItem.getKey(), (String) mapSetItem.getValue()));
			}
		}

		UrlEncodedFormEntity urlEncodedFormEntity = null;
		try {
			urlEncodedFormEntity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Parameter가 Null 이 아니면.
		if (urlEncodedFormEntity != null) {
			log("[[Fat Body]]");
			httpPut.setEntity(urlEncodedFormEntity);
		} else {
			log("[[Null body.]]");
		}
		return httpPut;
	}
	
	/***
	 * PUT Case ( File )
	 * 
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestPutFile(RequestBundle requestBundle) {
		HttpPut httpPut = new HttpPut(urlConverter(requestBundle));

//		httpHeader.put("Content-Type", "multipart/form-data");
		
		httpHeader.headerMap.remove("Content-Type");
		
		MultipartEntity multiPartEntity = new MultipartEntity();
		FileBody fileBody = new FileBody(requestBundle.getUploadFile(), "multipart/form-data");
		multiPartEntity.addPart(requestBundle.getUploadFileKey(), fileBody);

		httpPut.setEntity(multiPartEntity);
		return httpPut;
	}

	
	/***
	 * GET Case
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestGet(RequestBundle requestBundle){
		HttpGet httpGet = new HttpGet(urlConverter(requestBundle));
		return httpGet;
	}
	
	/***
	 * DELETE Case
	 * @param requestBundle
	 * @return
	 */
	private HttpUriRequest requestDelete(RequestBundle requestBundle){
		HttpDelete httpDelete = new HttpDelete(urlConverter(requestBundle));
		return httpDelete;
	}
	
	private String urlConverter(RequestBundle requestBundle){
		log("URL : " + requestBundle.getUrl());
		log("paramSize : " + requestBundle.getParameters().size());
		if(requestBundle.getParameters().size() == 0){
			return requestBundle.getUrl();
		}
	
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> e : requestBundle.getParameters().entrySet()) {
			if (sb.length() > 0) {
				sb.append('&');
			}
			try {
				String key = e.getKey();
				Object value = e.getValue();
				String valueTarget = objToString(value);
				
				sb.append(URLEncoder.encode(key, "UTF-8")).append('=').append(URLEncoder.encode(valueTarget, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		String returnValue = requestBundle.getUrl() + "?" + sb.toString();
		
		log("ENCODED URL : [" + returnValue + "]");
		return returnValue;
	}
	
	
	private String objToString(Object obj) {
		if (obj == null) {
			return "";
		}
		return String.valueOf(obj);
	}
	
	
	
	/***
	 * Request Main 제어부
	 * @throws IOException 
	 * @throws  
	 */
	@Override
	public ResponseMessage request(RequestBundle requestBundle)
			throws PlanetXSDKException {
		
		HttpClient httpClient = null;
		
		// 0. Set Init Header Info : OAuth Info Set to Header
		setHeader();

		// Request Setup
		// 1. set Url(not null) and set parameters( null value is ok )
		if ("".equals(requestBundle.getUrl()) || requestBundle.getUrl() == null) {
			throw new PlanetXSDKException("ERR_CD_00002", PlanetXSDKConstants.ERR_CD_00002);
		}
		
		/*************** Accept / Content Type IS HERE *****************/
		// 2. set up to request payload type and result content type.
		String requestTypeString = PlanetXSDKConstants.getContentType(requestBundle.getRequestType());
		String returnTypeString = PlanetXSDKConstants.getContentType(requestBundle.getResponseType());

		log("REQ TYPE : " + requestTypeString);
		log("RES TYPE : " + returnTypeString);
		
		
		httpHeader.put("Content-Type", requestTypeString);
		httpHeader.put("Accept", returnTypeString);
		
		
		if ("error".equals(returnTypeString)) {
			throw new PlanetXSDKException("ERR_CD_00002", PlanetXSDKConstants.ERR_CD_00002);
		}

		// 3. set payload data (optional . )
		HttpUriRequest requestBase = null;
		switch (requestBundle.getHttpMethod()) {
		case PUT:
			if(requestBundle.getUploadFile() == null){
				if(CONTENT_TYPE.FORM.equals(requestBundle.getRequestType())){
					log("PUT : FORM URL ENCODED");
					requestBase = requestPutForm(requestBundle);
				}
				else
				{
					log("PUT : GENERAL");
					requestBase = requestPut(requestBundle);
				}
			}
			else
			{	
				log("PUT : FILEUPLOAD");
				requestBase = requestPutFile(requestBundle);
			}
			break;
		case POST:
			// Upload File의 경우 비어 있을 경우 FORM URL ENCODED 형식으로 보낼것인지, 
			if(requestBundle.getUploadFile() == null){
				if(CONTENT_TYPE.FORM.equals(requestBundle.getRequestType())){
					log("POST : FORM URL ENCODED");
					requestBase = requestPostForm(requestBundle);
				}
				else
				{
					log("POST : GENERAL");
					requestBase = requestPost(requestBundle);
				}
			}
			else
			{	
				log("POST : FILEUPLOAD");
				requestBase = requestPostFile(requestBundle);
			}
			break;
		case GET:
			log("GET");
			requestBase = requestGet(requestBundle);
			break;
		case DELETE:
			log("DELETE");
			requestBase = requestDelete(requestBundle);
			break;
		default:
			throw new PlanetXSDKException("ERR_CD_00001", PlanetXSDKConstants.ERR_CD_00001);
		}
		
		Map<String, String> map = httpHeader.getHeader();
		Set<Entry<String, String>> mapSet = map.entrySet();
		for(Entry<String, String> item : mapSet){
			requestBase.addHeader(item.getKey(), item.getValue());
		}
		
		if (PlanetXSDKConstants.IS_DEBUG) {
			try {
				for (Header hd : requestBase.getAllHeaders()) {
					log("HEAD Key : " + hd.getName() + " / " + "HEAD Value : " + hd.getValue());
				}
			} catch (Exception e) {
			}
		}

		if (requestBundle.getUrl().startsWith("https")) {
			log("Https!!");
			httpClient = getNewHttpClient();
		} else {
			log("Http!!");
			httpClient = new DefaultHttpClient();
		}
		
		// UA 값 추가 2014 03 03
		String defaultUserAgent = System.getProperty( "http.agent" );
		
		String userAgentString = defaultUserAgent + "; " + PlanetXSDKConstants.SDK_VERSION_PREFIX	+ PlanetXSDKConstants.SDK_VERSION;
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgentString);
		
		if (PlanetXSDKConstants.IS_DEBUG) {
			HttpParams hp = httpClient.getParams();
			String stringUa = hp.getParameter(CoreProtocolPNames.USER_AGENT) + "";
			log("User agent : " + stringUa);
		}
		
		
		HttpResponse response;
		ResponseMessage returnMessage = null;
		
		try {
			response = httpClient.execute(requestBase);
		
			HttpEntity resEntity = response.getEntity();
	
			returnMessage = new ResponseMessage();
			returnMessage.setStatusCode(response.getStatusLine().getStatusCode()+"");
			
			if ((resEntity != null) && (resEntity.getContentLength() > 0)) {
				returnMessage.setResultMessage(EntityUtils.toString(resEntity, "UTF-8"));
			} else {
				returnMessage.setResultMessage("");
			}
			
		} catch (IOException e) {
			throw new PlanetXSDKException("ERR_CD_10000", e.getMessage());
		}
		
		return returnMessage;
	}
	
	public ResponseMessage request(RequestBundle bundle, HttpMethod httpMethod) throws PlanetXSDKException {
		RequestBundle tossBundle = bundle;
		tossBundle.setHttpMethod(httpMethod);
		return request(tossBundle);
	}

	public ResponseMessage request(RequestBundle bundle, String url, HttpMethod httpMethod) throws PlanetXSDKException {
		RequestBundle tossBundle = bundle;
		tossBundle.setHttpMethod(httpMethod);
		tossBundle.setUrl(url);
		return request(tossBundle);
	}
	
	
	@Override
	public void request(final RequestBundle requestBundle, final RequestListener requestListener) throws PlanetXSDKException {
		if (requestListener == null) {
			throw new PlanetXSDKException("ERR_CD_00004", PlanetXSDKConstants.ERR_CD_00004);
		}
		new Thread() {
			@Override
			public void run() {
				try {
					ResponseMessage resultString = request(requestBundle);
					requestListener.onComplete(resultString);
				} 
				catch (PlanetXSDKException e) {
					requestListener.onPlanetSDKException(e);
				}
			}
		}.start();
	}
	
	public void request(RequestBundle bundle, HttpMethod httpMethod, RequestListener requestListener) throws PlanetXSDKException {
		RequestBundle tossBundle = bundle;
		tossBundle.setHttpMethod(httpMethod);
		tossBundle.setRequestListener(requestListener);
		request(tossBundle, requestListener);
	}

	public void request(RequestBundle bundle, String url, HttpMethod httpMethod, RequestListener requestListener) throws PlanetXSDKException {
		RequestBundle tossBundle = bundle;
		tossBundle.setHttpMethod(httpMethod);
		tossBundle.setUrl(url);
		tossBundle.setRequestListener(requestListener);
		request(tossBundle, requestListener);
	}

	private void log(String log) {
		if(PlanetXSDKConstants.IS_DEBUG) {
			System.out.println(log);
		}
	}
	
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
}
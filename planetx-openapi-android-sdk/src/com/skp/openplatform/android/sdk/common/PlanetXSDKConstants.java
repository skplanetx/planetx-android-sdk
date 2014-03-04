package com.skp.openplatform.android.sdk.common;

public class PlanetXSDKConstants {

	public static String SDK_VERSION_PREFIX = "SDK Android/";
	public static String SDK_VERSION = "1.04";
	
	public static boolean IS_DEBUG = true;
	
	public static enum CONTENT_TYPE {
		XML, JSON, FORM, JS, KML, KMZ, GEO
	};

	public static enum HttpMethod {
		GET, PUT, POST, DELETE
	};

	public static String getContentType(CONTENT_TYPE type) {
		String contentTypeString = "";

		switch (type) {
		case XML:
			contentTypeString = "application/xml";
			break;
		case JSON:
			contentTypeString = "application/json";
			break;
		case FORM:
			contentTypeString = "application/x-www-form-urlencoded";
			break;
		case JS:
			contentTypeString = "application/javascript";
			break;
		case KML:
			contentTypeString = "application/vnd.google-earth.kml+xml";
			break;
		case KMZ:
			contentTypeString = "application/vnd.google-earth.kmz";
			break;
		case GEO:
			contentTypeString = "application/geo+json";
			break;
		default:
			contentTypeString = "error";
			break;
		}

		return contentTypeString;

	}
	
	public static final String HEADER_APP_KEY 		= "appKey";
	public static final String HEADER_ACCESS_TOKEN 	= "access_token";
	public static final String HEADER_REFRESH_TOKEN 	= "refresh_token";

	/*** 
	 * Error Code
	 */
	public static final String ERR_CD_00001 = "HttpMethod is null";
	public static final String ERR_CD_00002 = "Url is null or empty";

	public static final String ERR_CD_00004 = "ASynchronous Listener is null";
	public static final String ERR_CD_00005 = "Header infomation is null";
	
}

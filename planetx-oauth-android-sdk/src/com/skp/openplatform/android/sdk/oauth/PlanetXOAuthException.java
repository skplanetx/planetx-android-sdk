package com.skp.openplatform.android.sdk.oauth;

/***
 * SKP - Android SDK에서 Framework와 통신하는데 사용하는 응답메시지의 구조 및 Excpetion 처리를 위한
 * Wrapping Class.
 * 
 * @author lhjung
 * 
 */
public class PlanetXOAuthException extends Exception {

	private static final long serialVersionUID = 4435605378608634192L;

	/***
	 * 에러 코드
	 */
	private String code;
	/***
	 * 에러 메시지
	 */
	private String message;

	/***
	 * SKPOPException 생성자
	 */
	public PlanetXOAuthException() {
		super();
		this.code = "";
		this.message = "";
	}

	/***
	 * SKPOPException 생성자
	 * @param code 에러코드
	 * @param message 에러메시지
	 */
	public PlanetXOAuthException(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	/***
	 * 오류 코드를 반환
	 * @return 오류 코드
	 */
	public String getCode() {
		return code;
	}

	/***
	 * 오류 코드를 설정
	 * @param code 오류코드
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/***
	 * 오류 메시지를 반환
	 * @return 오류 메시지
	 */
	public String getMessage() {
		return message;
	}

	/***
	 * 오류 메시지를 설정
	 * @param message 오류 메시지
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/***
	 * 오류 상황 발생시 출력 문구 수정
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("code : " + code + "\n");
		sb.append("message : " + message + "\n");
		sb.append(super.toString());
		return sb.toString();
	}

}

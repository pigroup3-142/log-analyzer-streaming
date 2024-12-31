package models;

import scala.Serializable;

public class ApacheAccessLog implements Serializable{
	/**
	 * if Object use in Spark Streaming muse implements Serializable interface and 
	 * define serialVersionUID 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ipAddress;
	private String clientIdentd;
	private String userId;
	private String dateTime;
	private String method;
	private String endpoint;
	private String protocal;
	private Integer responseCode;
	private Long contentSize;
	
	public ApacheAccessLog(String ipAddress, String clientIdentd, String userId, String dateTime, String method,
			String endpoint, String protocal, Integer responseCode, Long contentSize) {
		super();
		this.ipAddress = ipAddress;
		this.clientIdentd = clientIdentd;
		this.userId = userId;
		this.dateTime = dateTime;
		this.method = method;
		this.endpoint = endpoint;
		this.protocal = protocal;
		this.responseCode = responseCode;
		this.contentSize = contentSize;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getClientIdentd() {
		return clientIdentd;
	}
	
	public void setClientIdentd(String clientIdentd) {
		this.clientIdentd = clientIdentd;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public String getProtocal() {
		return protocal;
	}
	
	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}
	
	public Integer getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}
	
	public Long getContentSize() {
		return contentSize;
	}
	
	public void setContentSize(Long contentSize) {
		this.contentSize = contentSize;
	}
	
}
	
	

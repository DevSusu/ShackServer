package org.soma.tleaf.accesskey;

import java.text.ParseException;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.soma.tleaf.util.ISO8601;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccessKey {
	
	@JsonProperty("_id")
	private String accessKey;
	
	@JsonProperty("_rev")
	private String rev;
	
	private String userId;
	private String appId;

	// Both field will be in ISO 8601 format.
	private String validFrom;
	private String validTo;
	
	private boolean valid;
	
	public boolean isValid ( String userId ,String appId ) {
		
		// all FIVE values must be true to return true
		try {
			
			if ( 	valid && 
					ISO8601.isFirstEarlier(validFrom, ISO8601.now()) && 
					ISO8601.isFirstEarlier(ISO8601.now(), validTo) && 
					userId.matches(this.userId) && 
					appId.matches(this.appId) 	) return true;
			
			else return false;
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			return false;
			
		}
		
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getRev() {
		return rev;
	}

	public String getUserId() {
		return userId;
	}

	public String getValidFrom() {
		return validFrom;
	}

	public String getValidTo() {
		return validTo;
	}
	
	public boolean getvalid() {
		return valid;
	}

}

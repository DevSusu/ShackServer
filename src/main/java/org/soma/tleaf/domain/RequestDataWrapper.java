/**
 * 
 */
package org.soma.tleaf.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with Eclipse IDE
 * Author : RichardJ 
 * Date   : Oct 17, 2014 9:01:57 PM
 * Description : 
 */
public class RequestDataWrapper {
	private HashMap<String, Object> serviceData;

	public HashMap<String, Object> getserviceData() {
		return serviceData;
	}

	public void setData(HashMap<String, Object> serviceData) {
		this.serviceData = serviceData;
	}	

}

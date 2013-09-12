package com.easyrest;

import java.net.URI;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

class HttpClientInvoker {
	
	private Logger  logger = Logger.getLogger(HttpClientInvoker.class);
	
	public HttpResponse invoke(URI uri, Map<String, String> queryStrings) {
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri);
		
		try {
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() == 400) {
				throw new RuntimeException("Resoure not found");
			}
			
			return response;
			
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}  
	}
}

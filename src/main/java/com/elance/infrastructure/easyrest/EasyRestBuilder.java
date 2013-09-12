package com.elance.infrastructure.easyrest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.http.HttpResponse;

/**
 * 
 *  http://tutorials.jenkov.com/java-reflection/dynamic-proxies.html
 *	http://www.journaldev.com/721/java-annotations-tutorial-with-custom-annotation-example-and-parsing-using-reflection
 *
 * @author gvazquez
 *
 */
public class EasyRestBuilder<T> {

	private final Class<T> clientClass;
	
	
	// TODO lets use a conf object to initialize the instance)
	public EasyRestBuilder(Class<T> clientClass) {
		this.clientClass = clientClass;			
	}
	
	public Class<String> getMyType(){
        return String.class;
    }
	
	@SuppressWarnings("unchecked")
	public T createClient(String host) {
		if (clientClass == null) {
			throw new RuntimeException("Invalid generic type!");
		}
		
		InvocationHandler handler = new MyInvocationHandler(host);		
		T proxy = (T) Proxy.newProxyInstance(clientClass.getClassLoader(),
		                            new Class[] { clientClass },
		                            handler);
		
		return proxy;
	}
	
	private class MyInvocationHandler implements InvocationHandler {

		private String host;
		
		public MyInvocationHandler(String host) {
			this.host = host;
		}

		
		public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {

			if (!(arg1 instanceof AccessibleObject)) {
				throw new IllegalArgumentException("No annotations found in method!");
			}
			
			AccessibleObject method = (AccessibleObject) arg1;
			Class<?> client = arg1.getDeclaringClass().getClass();
						
			String contextPath = "";
			Path classPathAnno = client.getAnnotation(javax.ws.rs.Path.class);
			if (classPathAnno != null) {
				contextPath = classPathAnno.value();
			}
			
			// asumes @Get for the moment
			GET methodGetAnnotation = method.getAnnotation(javax.ws.rs.GET.class);
			if (methodGetAnnotation == null) {
				throw new RuntimeException("Only Get method is supported");
			}
			
			String methodPath = "";
			Path methodPathAnnotation = method.getAnnotation(javax.ws.rs.Path.class);
			if (methodPathAnnotation != null) {
				methodPath = methodPathAnnotation.value();				 
			}

			// path params
			Annotation[][] methodParamsAnnotations = arg1.getParameterAnnotations();
			for (int i = 0; i < methodParamsAnnotations.length; i++) {
				Annotation[] annotations = methodParamsAnnotations[i];
				for (Annotation annotation: annotations) {
					// TODO: ok, need to match this value against the params in the method path string (just for now position-based) 
					if (annotation instanceof javax.ws.rs.PathParam) {
						String  param = arg2[i].toString();
						String paramName = ((javax.ws.rs.PathParam) annotation).value();
						contextPath += methodPath.replaceFirst("\\{" + paramName + "\\}", param);
					} 
				}
			}
			
			//
			if (!(host.startsWith("http://") || host.startsWith("https://"))) {
				host = "http://" + host;
			}			
						
			String baseContext = String.format("%s/%s", host, contextPath);
			URI uri = URI.create(baseContext);
			Map<String, String> queryStrings = new HashMap<String, String>();
			HttpClientInvoker invoker = new HttpClientInvoker();
			HttpResponse response = invoker.invoke(uri, queryStrings);
			
			// Get the response
			StringBuffer result = new StringBuffer();			
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));			    
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			} 
			
			return result.toString();
		}
	}

}

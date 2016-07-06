package com.miloFramework.control;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

import com.miloFramework.control.ConfigXMLReader.ControllerConfig;
import com.miloFramework.control.ConfigXMLReader.RequestMap;
import com.miloFramework.service.ServiceContext;
import com.miloFramework.service.ServiceDispatcher;
import com.miloFramework.service.ServiceExecuteChain;

public class RequestHandler {
	
	public static final String module = RequestHandler.class.getName();
	  
	  
	public static RequestHandler getRequestHandler(ServletContext servletContext) {
		    String handlerName = servletContext.getServletContextName()+'_'+module;
	    RequestHandler rh = (RequestHandler) servletContext.getAttribute(handlerName);
	    if (rh == null) {
	        rh = new RequestHandler();
	        servletContext.setAttribute(handlerName, rh);
	        rh.init(servletContext);
	    }
	    return rh;
	}
	  
	protected ServletContext servletContext = null;
	protected URL controllerConfigURL = null;
	ControllerConfig controllerConfig = null;
	protected ServiceDispatcher serviceDispatcher;
	  
	protected void init(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.controllerConfigURL = ConfigXMLReader.getControllerConfigURL(servletContext);
        controllerConfig =  ConfigXMLReader.getControllerConfig(this.controllerConfigURL);
	    for (RequestMap requestMap : controllerConfig.requestMapMap.values()) {
	    	ServiceContext.getServiceContext().register(requestMap.uri,requestMap.serviceMaps);
		}
	    serviceDispatcher = ServiceDispatcher.getServiceDispatcher();
	}
	
	
	
	public void doReuqest(String uri,HttpServletRequest request,HttpServletResponse response){
		
		ServiceExecuteChain serviceExecuteChain = ServiceContext.getServiceContext().getServiceExecuteChain(uri);
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		for (String parameterName : request.getParameterMap().keySet()) {
			params.put(parameterName, WebUtils.findParameterValue(request.getParameterMap(), parameterName));
		}
		if(serviceExecuteChain!=null){
			try {
				serviceDispatcher.run(serviceExecuteChain, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

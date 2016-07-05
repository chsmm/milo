package com.miloFramework.control;

import java.net.URL;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.miloFramework.control.ConfigXMLReader.ControllerConfig;
import com.miloFramework.control.ConfigXMLReader.RequestMap;
import com.miloFramework.service.ServiceContext;
import com.miloFramework.service.ServiceDispatcher;

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
	protected ServiceDispatcher serviceDispatcher;
	  
	public void init(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.controllerConfigURL = ConfigXMLReader.getControllerConfigURL(servletContext);
        ControllerConfig controllerConfig =  ConfigXMLReader.getControllerConfig(this.controllerConfigURL);
	    for (RequestMap requestMap : controllerConfig.requestMapMap.values()) {
	    	ServiceContext.getServiceContext().register(requestMap.uri,requestMap.serviceMaps);
		}
	    serviceDispatcher = ServiceDispatcher.getServiceDispatcher();
	}
}

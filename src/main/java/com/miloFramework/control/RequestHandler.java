package com.miloFramework.control;

import java.net.URL;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

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
	protected WebApplicationContext webApplicationContext;
	  
	public void init(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.webApplicationContext =  ContextLoader.getCurrentWebApplicationContext(); 
        this.controllerConfigURL = ConfigXMLReader.getControllerConfigURL(servletContext);
	    //ConfigXMLReader.getControllerConfig(this.controllerConfigURL);
	}
}

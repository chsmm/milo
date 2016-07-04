package com.miloFramework.control;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Table.Cell;
import com.google.common.collect.Tables;
import com.miloFramework.base.util.UtilXml;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ConfigXMLReader {
		private static final ConcurrentHashMap<URL,ControllerConfig> controllerCache = new ConcurrentHashMap<URL,ControllerConfig>(64);
	    
		public static final String module = ConfigXMLReader.class.getName();
	    public static final String controllerXmlFileName = "/WEB-INF/controller.xml";
	    private static final Map<String, Class<?>> primitiveMap = FastMap.newInstance();
	    
	    
	    {

	    	registered("long",long.class);
	    	registered("boolean", boolean.class);
	    	registered("char", char.class);
	    	registered("int",int.class);
	    	registered("double",double.class);
	    	registered("float",float.class);
	    	registered("byte", byte.class);
	    	registered("short", short.class);	   
	    	registered("Integer",Integer.class);
	    	registered("Float",Float.class);
	    	registered("Double",Double.class);
	    	registered("character",Character.class);
	    	registered("Long",Long.class);
	    	registered("Boolean",Boolean.class);
	    	registered("Byte", Byte.class);
	    	registered("Short", Short.class);
	    	registered("date",Date.class);
	    	registered("string",String.class);
	    	registered("object",Object.class);
	    }
	    
	    private static void registered(String className,Class<?> clazz){
	    	primitiveMap.put(className, clazz);
	    }
	    
	    
	    public static URL getControllerConfigURL(ServletContext context) {
	        try {
	            return context.getResource(controllerXmlFileName);
	        } catch (MalformedURLException e) {
	            return null;
	        }
	    }
	    
	    
	    public static URL getControllerConfigURL(ServletContext context,String url) {
	        try {
	        	return StringUtils.isEmpty(url) ? context.getResource(controllerXmlFileName): context.getResource(url);
	        } catch (MalformedURLException e) {
	            return null;
	        }
	    }
	    
	    public static ControllerConfig getControllerConfig(URL url) {
	        ControllerConfig controllerConfig = controllerCache.get(url);
	        if (controllerConfig == null) {
	            controllerConfig = controllerCache.putIfAbsent(url, new ControllerConfig(url));
	        }
	        return controllerConfig;
	    }
	    
	    public static Element loadDocument(URL location) {
	        Document document;
	        try {
	            document = UtilXml.readXmlDocument(location, true);
	            Element rootElement = document.getDocumentElement();
	            return rootElement;
	        } catch (Exception e) {
	        }
	        return null;
	    }
	    
	    public static class ControllerConfig {
	    	 public URL url;
	    	 private List<URL> includes = FastList.newInstance();
	    	 public Map<String, RequestMap> requestMapMap = FastMap.newInstance();
	    	 public ControllerConfig(URL url) {
	    		 this.url=url;
	    		 Element rootElement = loadDocument(url);
	    		 
	    		 if (rootElement != null) {
	                 long startTime = System.currentTimeMillis();

	                 loadIncludes(rootElement);
	                 loadRequestMap(rootElement);

                     double totalSeconds = (System.currentTimeMillis() - startTime)/1000.0;
                     System.out.println("controller loaded: " + totalSeconds + "s, " + this.requestMapMap.size() + " requests ");
                 
	             }
			 }
	    	 
    	    protected void loadIncludes(Element rootElement) {
                for (Element includeElement: UtilXml.childElementList(rootElement, "include")) {
                    String includeLocation = includeElement.getAttribute("location");
                    if (StringUtils.isNotEmpty(includeLocation)) {
                        try {
                        	this.includes.add(new URL(includeLocation));
                        } catch (MalformedURLException mue) {
                            System.out.println( "Error processing include at [" + includeLocation + "]:" + mue.toString());
                        }
                    }
                }
            }
    	    
    	    
    	    protected void loadRequestMap(Element root) {
                for (Element requestMapElement: UtilXml.childElementList(root, "request-map")) {
                    RequestMap requestMap = new RequestMap(requestMapElement);
                    this.requestMapMap.put(requestMap.uri, requestMap);
                }
            }
	    }
	    
	    
	    
	    public static class RequestMap {
	        public String uri;
	        
	        public List<Cell<String, String, ServiceMap>> serviceMaps = FastList.newInstance();;

	        public boolean auth = false;
	        
	        public boolean token=false;

	        public Map<String, RequestResponse> requestResponseMap = FastMap.newInstance();

	        public RequestMap(Element requestMapElement) {
	            // Get the URI info
	            this.uri = requestMapElement.getAttribute("uri");

	            // Check for auth
	            String auth = requestMapElement.getAttribute("auth");
	            if (StringUtils.isNotBlank(auth)) {
	                this.auth = "true".equals(auth);
	            }
	            
	            // Check for token
	            String token = requestMapElement.getAttribute("token");
	            if (StringUtils.isNotBlank(token)) {
	                this.token = "true".equals(token);
	            }

	            // Get the service(s)
	            List<? extends Element> services= UtilXml.childElementList(requestMapElement, "service");
	            if(services!=null && !services.isEmpty()){
	            	 for (Element serviceElement: services){
	            		 ServiceMap serviceMap= new ServiceMap(serviceElement);
	            		 serviceMaps.add(Tables.immutableCell(serviceMap.name, serviceMap.method, serviceMap));
	 	            }
	            }
	          


	            // Get the response(s)
	         /*   for (Element responseElement: UtilXml.childElementList(requestMapElement, "response")) {
	                RequestResponse response = new RequestResponse(responseElement);
	                requestResponseMap.put(response.name, response);
	            }*/
	        
	        }
	    }
	    public static class ServiceMap {
	        public String name;
	        public String method;
	        public String mode;
	        public Integer timeout;
	        public Map<String, Parameter> parameterMap = FastMap.newInstance();

	        public ServiceMap(Element serviceElement) {
	            this.name = serviceElement.getAttribute("name");
	            this.method = serviceElement.getAttribute("method");
	            this.mode = serviceElement.getAttribute("mode");
	            String timeOut= serviceElement.getAttribute("timeout");
	            if (StringUtils.isNotBlank(timeOut)) {
	                this.timeout = Integer.parseInt(timeOut);
	            }
	            // Get the parameterMap(s).	      
	            List<? extends Element> parameters= UtilXml.childElementList(serviceElement, "parameter");
	            if(parameters!=null && !parameters.isEmpty()){
	            	 for (Element responseElement: parameters){
	 	            	
	 	            }
	            }
	           
	        }
	    }
	    
	    
	    public static class Parameter {
	        public String name;
	        public String type;
	        public String mode;

	        public Parameter(Element serviceElement) {
	            this.name = serviceElement.getAttribute("name");
	            this.type = serviceElement.getAttribute("type");
	            this.mode = serviceElement.getAttribute("mode");
	        }
	    }
	    
	    public static final RequestResponse emptyNoneRequestResponse = RequestResponse.createEmptyNoneRequestResponse();
	    public static class RequestResponse {
	        public String name;
	        public String type;
	        public String value;
	        public String statusCode;
	        public boolean saveLastView = false;
	        public boolean saveCurrentView = false;
	        public boolean saveHomeView = false;
	        public Map<String, String> redirectParameterMap = FastMap.newInstance();
	        public Map<String, String> redirectParameterValueMap = FastMap.newInstance();

	        public RequestResponse(Element responseElement) {
	            this.name = responseElement.getAttribute("name");
	            this.type = responseElement.getAttribute("type");
	            this.value = responseElement.getAttribute("value");
	            this.statusCode = responseElement.getAttribute("status-code");
	            this.saveLastView = "true".equals(responseElement.getAttribute("save-last-view"));
	            this.saveCurrentView = "true".equals(responseElement.getAttribute("save-current-view"));
	            this.saveHomeView = "true".equals(responseElement.getAttribute("save-home-view"));
	      /*      for (Element redirectParameterElement: UtilXml.childElementList(responseElement, "redirect-parameter")) {
	                if (UtilValidate.isNotEmpty(redirectParameterElement.getAttribute("value"))) {
	                    this.redirectParameterValueMap.put(redirectParameterElement.getAttribute("name"), redirectParameterElement.getAttribute("value"));
	                } else {
	                    String from = redirectParameterElement.getAttribute("from");
	                    if (UtilValidate.isEmpty(from)) from = redirectParameterElement.getAttribute("name");
	                    this.redirectParameterMap.put(redirectParameterElement.getAttribute("name"), from);
	                }
	            }*/
	        }

	        public RequestResponse() { }

	        public static RequestResponse createEmptyNoneRequestResponse() {
	            RequestResponse requestResponse = new RequestResponse();
	            requestResponse.name = "empty-none";
	            requestResponse.type = "none";
	            requestResponse.value = null;
	            return requestResponse;
	        }
	    }
	
}

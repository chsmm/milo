package com.miloFramework.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javolution.util.FastMap;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.miloFramework.control.ConfigXMLReader.Parameter;
import com.miloFramework.control.ConfigXMLReader.ServiceMap;

public class ServiceContext {
	
	
	
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
	
    private ClassLoader loader;
    
    
    private ConcurrentMap<String, List<ServiceMap>> serviceMaps = Maps.newConcurrentMap();
	
	private ConcurrentMap<String, MethodAccess> serviceMethodAccessMap = Maps.newConcurrentMap();
	
	private Table<String, String, ParameterMetaData> serviceMethodParameterMap = HashBasedTable.create();
	private Table<String, String, ReturnMetaData> serviceMethodReturnMap = HashBasedTable.create();
	
	
	private static final ServiceContext SERVICE_CONTEXT = new ServiceContext();
	
	
	protected WebApplicationContext webApplicationContext ;
	
	protected ServiceContext() {
		loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
            System.out.println("Unable to get classloader; using system");
            loader = ClassLoader.getSystemClassLoader();
        }
		webApplicationContext =  ContextLoader.getCurrentWebApplicationContext();
	}
	
	
	public static ServiceContext getServiceContext(){
		return SERVICE_CONTEXT;
	}
	
    public WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}

    
    public ServiceExecuteChain getServiceExecuteChain(String uri){
    	return this.serviceMaps.containsKey(uri) ? new ServiceExecuteChain(this, this.serviceMaps.get(uri)): null;
    }
    
  
    public MethodAccess getServiceMethodAccess(String service){
    	return serviceMethodAccessMap.get(service);
    }
    
    public ParameterMetaData getParameterMetaData(String service,String method){
    	return serviceMethodParameterMap.get(service, method);
    }
    
    public ReturnMetaData getReturnMetaData(String service,String method){
    	return serviceMethodReturnMap.get(service, method);
    }
    
    

	public void register(String uri ,List<ServiceMap> serviceMaps) {
    	if(StringUtils.isBlank(uri)||serviceMaps==null||serviceMaps.isEmpty())return;
    	if(this.serviceMaps.containsKey(uri))return;
    	this.serviceMaps.putIfAbsent(uri, serviceMaps);
    	for (ServiceMap serviceMap : serviceMaps) {
    		register(serviceMap);
		}
    	
    }
    
    protected void register(ServiceMap serviceMap) {
    	if(serviceMap==null) return ;
    	MethodAccess serviceMethodAccess =serviceMethodAccessMap.get(serviceMap.name) ;
    	if(serviceMethodAccess==null){
	    	Object serviceObject = webApplicationContext.getBean(serviceMap.name);
	    	if(serviceObject==null){
	    		System.out.println(serviceMap.name + " service not found ");
	    		return;
	    	}
	    	serviceMethodAccess = MethodAccess.get(serviceObject.getClass());
	    	serviceMethodAccessMap.putIfAbsent(serviceMap.name, serviceMethodAccess);
    	}
    	List<Parameter> parameters = serviceMap.parameterList;
    	if(parameters.isEmpty()){
    		int index = serviceMethodAccess.getIndex(serviceMap.method);
    		serviceMethodParameterMap.put(serviceMap.name, serviceMap.method, new ParameterMetaData(index, null,null, serviceMap.mode, serviceMap.timeout));
    	}else{
    	    // load the class
    		List<Class<?>> parameterTypes = new LinkedList<Class<?>>();
    		Class<?> parameterType=null;
    		Map<Class<?>, String> parameterTypeMap = new LinkedHashMap<Class<?>, String>();
    		for (Parameter parameter : parameters) {
    			parameterType = primitiveMap.get(parameter.type);
    			if(parameterType != null){
		            try {
		            	parameterType = loader.loadClass(parameter.type);
		            } catch (ClassNotFoundException e) {
		                throw new RuntimeException("Cannot locate class " + parameter.type, e);
		            }
		            if (parameterType == null) {
		                throw new RuntimeException("class not loaded "+parameter.type);
		            }
	            }
    			if(ObjectUtils.equals(parameter.mode,"IN")){
    				 parameterTypes.add(parameterType);
    				 parameterTypeMap.put(parameterType, parameter.name);
    			}else{
    				if(serviceMethodReturnMap.get(serviceMap.name, serviceMap.method)!=null)throw new RuntimeException("The service "+serviceMap.name+" "+serviceMap.method+" method with multiple return parameter");
    				serviceMethodReturnMap.put(serviceMap.name, serviceMap.method, new ReturnMetaData(parameterType,parameter.name,parameter.mode));
    			}
	           
			}
    		Class<?>[] classs = parameterTypes.toArray(new Class<?>[0]);
    		int index = serviceMethodAccess.getIndex(serviceMap.method, classs);
    		serviceMethodParameterMap.put(serviceMap.name, serviceMap.method, new ParameterMetaData(index, classs,parameterTypeMap, serviceMap.mode, serviceMap.timeout));
    	}
    	
    }
    
    public static class ParameterMetaData{
    	public int index;
    	public Class<?>[] parameterTypes;
    	public Map<Class<?>, String> parameterTypeMap;
    	public String mode;
    	public Integer timeout;
    	
    	public ParameterMetaData(int index,Class<?>[] parameterTypes,Map<Class<?>, String> parameterTypeMap,String mode,Integer timeout) {
    		this.index = index;
    		this.parameterTypes = parameterTypes;
    		this.parameterTypeMap = parameterTypeMap;
    		this.mode = mode;
    		this.timeout = timeout;
		}
    }
    
    public static class ReturnMetaData{
    	
    	public Class<?> parameterType;
    	public String name;
    	public String mode;
    	
    	public ReturnMetaData(Class<?> parameterType,String name,String mode) {
    		this.parameterType = parameterType;
    		this.name = name;
    		this.mode = mode;
		}
    }
}

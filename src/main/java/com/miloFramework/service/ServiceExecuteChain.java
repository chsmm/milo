package com.miloFramework.service;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.commons.lang.ObjectUtils;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.miloFramework.control.ConfigXMLReader.ServiceMap;
import com.miloFramework.service.ServiceContext.ParameterMetaData;
import com.miloFramework.service.ServiceContext.ReturnMetaData;

public class ServiceExecuteChain {
	
	public static final String ASYNC_MODE = "async";
	public static final String SYNC_MODE = "sync";
	
	protected List<ServiceMap> serviceMaps;
	
	protected int currentPosition = 0;
	
	protected ServiceContext serviceContext;
	
	public ServiceExecuteChain(ServiceContext serviceContext,List<ServiceMap> serviceMaps) {
		this.serviceContext=serviceContext;
		this.serviceMaps = serviceMaps;
	}
	
	
	public void execute(ServiceDispatcher dispatcher,Map<String, ? extends Object> params,Map<String, Object> result) throws Exception{
		if (currentPosition == serviceMaps.size()) {
			return;
		} else {
			currentPosition++;
			ServiceMap serviceMap = serviceMaps.get(currentPosition - 1);
			Object service = serviceContext.getService(serviceMap.name);
			MethodAccess serviceMethodAccess = serviceContext.getServiceMethodAccess(serviceMap.name);
			ParameterMetaData parameterMetaData = serviceContext.getParameterMetaData(serviceMap.name, serviceMap.method);
			ReturnMetaData returnMetaData = serviceContext.getReturnMetaData(serviceMap.name, serviceMap.method);
			if(ObjectUtils.equals(serviceMap.mode, ASYNC_MODE)){
				dispatcher.runAsync(service,serviceMethodAccess, parameterMetaData, returnMetaData, params);
			}else{
				dispatcher.runSync(service,serviceMethodAccess, parameterMetaData, returnMetaData, params);
			}
		}
		
		
	}
	
	
	

}

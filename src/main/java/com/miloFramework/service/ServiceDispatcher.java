package com.miloFramework.service;

import java.util.Map;

import javolution.util.FastMap;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.miloFramework.service.ServiceContext.ParameterMetaData;
import com.miloFramework.service.ServiceContext.ReturnMetaData;

public class ServiceDispatcher {
	
	
	protected static final ServiceDispatcher SERVICE_DISPATCHER = new ServiceDispatcher();
	
	protected ServiceDispatcher() {}
	
	public static ServiceDispatcher getServiceDispatcher(){
		return SERVICE_DISPATCHER;
	}
	
	
	public Map<String, Object> run(ServiceExecuteChain serviceExecuteChain, Map<String, ? extends Object> params) throws Exception{
		Map<String, Object> result = FastMap.newInstance();
		serviceExecuteChain.execute(this, params,result);
		return result;
	}

	
	protected Map<String, Object> runSync(MethodAccess serviceMethodAccess, ParameterMetaData parameterMetaData, ReturnMetaData returnMetaData,Map<String, ? extends Object> params) throws Exception{
		
		
		return null;
	}
	
	protected Map<String, Object> runAsync(MethodAccess serviceMethodAccess, ParameterMetaData parameterMetaData, ReturnMetaData returnMetaData,Map<String, ? extends Object> params) throws Exception{
		
		
		return null;
	}
	
}

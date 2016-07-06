package com.miloFramework.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.miloFramework.base.util.ObjectType;
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

	
	protected Map<String, Object> runSync(Object service,MethodAccess serviceMethodAccess, ParameterMetaData parameterMetaData, ReturnMetaData returnMetaData,Map<String, ? extends Object> params) throws Exception{
		String jsonParams = JSON.toJSONString(params);
		Object result = null;
		if(parameterMetaData.parameterTypes==null){
			result = serviceMethodAccess.invoke(service, parameterMetaData.index);
		}else{
			List<Object> parameterValues = new ArrayList<Object>(parameterMetaData.parameterTypes.length);
			for (Class<?> parameterType : parameterMetaData.parameterTypes) {
				String parameterName = parameterMetaData.parameterTypeNameMap.get(parameterType);
				if(ObjectType.isPrimitive(parameterType.getName())){
					parameterValues.add(params.get(parameterName));
				}else{
					parameterValues.add(JSON.parseObject(jsonParams, parameterType));
				}
				
			}
			result = serviceMethodAccess.invoke(service, parameterMetaData.index, parameterMetaData.parameterTypes, parameterValues);
		}
		System.out.println(result);
		
		return null;
	}
	
	protected Map<String, Object> runAsync(Object service,MethodAccess serviceMethodAccess, ParameterMetaData parameterMetaData, ReturnMetaData returnMetaData,Map<String, ? extends Object> params) throws Exception{
		
		
		return null;
	}
	
}

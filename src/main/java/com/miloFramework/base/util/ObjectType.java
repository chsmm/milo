package com.miloFramework.base.util;

import java.util.Date;
import java.util.Map;

import javolution.util.FastMap;

public class ObjectType {
	
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
    	/*registered("object",Object.class);*/
    }
    
    private static void registered(String className,Class<?> clazz){
    	primitiveMap.put(className, clazz);
    }
    
    public static boolean isPrimitive(String type){
    	return primitiveMap.containsKey(type);
    }
    
    public static Class<?> getPrimitive(String type){
    	return primitiveMap.get(type);
    }
}

package cn.link.common;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MyGson {

	private static Gson gson = new Gson();
	
	public static String getString(Object obj){
		return gson.toJson(obj);
	}
	
	public static <T> T getObject(String json,Class<T> clazz){
		return gson.fromJson(json, clazz);
	}
	
	public static <T> List<T> getList(String json,Class<T> clazz){
		return gson.fromJson(json, new TypeToken<List<T>>(){}.getType());
	}
}

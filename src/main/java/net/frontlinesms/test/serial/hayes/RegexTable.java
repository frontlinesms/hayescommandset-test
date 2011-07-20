package net.frontlinesms.test.serial.hayes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

class RegexTable<T> {
	private final Map<Object, T> map = new LinkedHashMap<Object, T>();
	
	public T put(Object key, T value) {
		return map.put(key, value);
	}
	
	public T get(String token, T defaultValue) {
		for(Entry<Object, T> e : map.entrySet()) {
			Object k = e.getKey();
			if((k instanceof String && k.equals(token)) ||
					(k instanceof Pattern && ((Pattern) k).matcher(token).matches())) {
				return e.getValue();
			} 
		}
		return defaultValue;
	}
}

package com.github.zerowise.neptune.event;

import java.lang.reflect.ParameterizedType;

public abstract class EventListener<T extends Event> {

	private final int code;

	public EventListener() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		// 获取第一个类型参数的真实类型
		Class<T> clazz = (Class<T>) pt.getActualTypeArguments()[0];
		this.code = clazz.getAnnotation(EventType.class).value();
	}

	public int getCode() {
		return code;
	}

	public abstract void onEvent(T event);

}

package com.github.zerowise.neptune.invoke;

import java.lang.reflect.Method;

public class MethodInvoker {

	private final Method method;
	private final Object obj;

	public MethodInvoker(Method method, Object obj) {
		super();
		this.method = method;
		this.obj = obj;
	}

	public Object invoke(Object[] args) {
		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

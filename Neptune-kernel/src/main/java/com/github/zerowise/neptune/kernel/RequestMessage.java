package com.github.zerowise.neptune.kernel;

import java.lang.reflect.Method;

public class RequestMessage {

	private long id;
	private String serviceName;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] argments;

	public RequestMessage() {

	}

	public RequestMessage(long id, Method method, Object[] argments) {
		this.id = id;
		this.serviceName = method.getDeclaringClass().getName();
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.argments = argments;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArgments() {
		return argments;
	}

	public void setArgments(Object[] argments) {
		this.argments = argments;
	}
}

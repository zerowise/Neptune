package com.github.zerowise.neptune.invoke;

import java.util.Arrays;
import java.util.Objects;

public class MethodInvokerId {

	private String serviceName;
	private String methodName;
	private Class<?>[] parameterTypes;

	public MethodInvokerId() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MethodInvokerId(String serviceName, String methodName, Class<?>[] parameterTypes) {
		super();
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(parameterTypes);
		result = prime * result + Objects.hash(methodName, serviceName);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodInvokerId other = (MethodInvokerId) obj;
		return Objects.equals(methodName, other.methodName) && Arrays.equals(parameterTypes, other.parameterTypes)
				&& Objects.equals(serviceName, other.serviceName);
	}

}

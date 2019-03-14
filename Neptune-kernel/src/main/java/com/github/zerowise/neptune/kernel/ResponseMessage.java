package com.github.zerowise.neptune.kernel;

public class ResponseMessage {

	private long id;
	private Object result;
	private String errormsg;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

}

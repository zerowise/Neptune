package com.github.zerowise.neptune.proxy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcResult {

	private Lock lock = new ReentrantLock();
	private Condition succ = lock.newCondition();
	private Object result;
	private String error;

	public Object getResult() {
		try {
			lock.lock();
			succ.await();
			if (error != null) {
				throw new RuntimeException(error);
			}
			return result;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	public void onResult(Object result, String error) {
		try {
			lock.lock();
			this.result = result;
			this.error = error;
			succ.signal();
		} finally {
			lock.unlock();
		}
	}
}

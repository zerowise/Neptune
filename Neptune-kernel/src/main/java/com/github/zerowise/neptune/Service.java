package com.github.zerowise.neptune;

import com.github.zerowise.neptune.event.EventBus;

public interface Service {
	
	void regist(EventBus eventBus);

	void start() throws Throwable;
	
	void stop() throws Throwable;

}

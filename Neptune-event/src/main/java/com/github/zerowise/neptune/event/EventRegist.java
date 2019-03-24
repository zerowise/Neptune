package com.github.zerowise.neptune.event;

public interface EventRegist {
	
	void regist(Object obj, EventListener<? extends Event> eventListener);

	void unregist(Object obj);
}

package com.github.zerowise.neptune.event;

public interface EventRegist {
	
	void regist(EventListener<? extends Event> eventListener);

	void unregist(EventListener<? extends Event> eventListener);
}

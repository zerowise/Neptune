package com.github.zerowise.neptune.event;

public class Event {

	public final int code() {
		return this.getClass().getAnnotation(EventType.class).value();
	}

	public final <T> T cast() {
		return (T) this;
	}
}

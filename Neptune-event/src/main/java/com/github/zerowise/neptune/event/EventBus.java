package com.github.zerowise.neptune.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus implements EventNotify, EventRegist {

	private ConcurrentHashMap<Integer, List<EventListener<? extends Event>>> listeners;

	private ConcurrentHashMap<Class<?>, Integer> clazzCodes;

	public EventBus() {
		super();
		listeners = new ConcurrentHashMap<>();
	}

	@Override
	public void regist(EventListener<? extends Event> eventListener) {
		EventListener<? extends Event> value = Objects.requireNonNull(eventListener);

		listeners.computeIfAbsent(value.getCode(), key -> new CopyOnWriteArrayList<>()).add(value);

	}

	@Override
	public void unregist(EventListener<? extends Event> eventListener) {
		EventListener<? extends Event> value = Objects.requireNonNull(eventListener);
		listeners.computeIfPresent(value.getCode(), (key, vals) -> {
			vals.remove(value);
			return vals;
		});
	}

	@Override
	public void notify(Event event) {
		Event e = Objects.requireNonNull(event);
		int code = clazzCodes.computeIfAbsent(e.getClass(), key -> e.code());

		List<EventListener<? extends Event>> list = listeners.get(code);

		if (list == null || list.isEmpty()) {
			return;
		}

		list.forEach(register -> register.onEvent(e.cast()));
	}

}

package com.github.zerowise.neptune.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus implements EventNotify, EventRegist {

    private ConcurrentHashMap<Integer, List<EventListenerHolder>> listeners;

    private ConcurrentHashMap<Class<?>, Integer> clazzCodes;

    public EventBus() {
        super();
        this.listeners = new ConcurrentHashMap<>();
        this.clazzCodes = new ConcurrentHashMap<>();
    }

    @Override
    public void regist(Object obj, EventListener<? extends Event> eventListener) {
        EventListenerHolder holder = new EventListenerHolder(obj, eventListener);

        listeners.computeIfAbsent(holder.getCode(), key -> new CopyOnWriteArrayList<>()).add(holder);

    }

    @Override
    public void unregist(Object obj) {
        Object value = Objects.requireNonNull(obj);
        listeners.values().forEach(list -> list.removeIf(val -> val.obj == value));
    }

    @Override
    public void notify(Event event) {
        Event e = Objects.requireNonNull(event);
        int code = clazzCodes.computeIfAbsent(e.getClass(), key -> e.code());

        List<EventListenerHolder> list = listeners.get(code);

        if (list == null || list.isEmpty()) {
            return;
        }

        list.forEach(register -> register.listener.onEvent(e.cast()));
    }


    private class EventListenerHolder {
        private final Object obj;
        private final EventListener<? extends Event> listener;

        private EventListenerHolder(Object obj, EventListener<? extends Event> listener) {
            this.obj = Objects.requireNonNull(obj);
            this.listener = Objects.requireNonNull(listener);
        }

        private int getCode() {
            return listener.getCode();
        }
    }

}

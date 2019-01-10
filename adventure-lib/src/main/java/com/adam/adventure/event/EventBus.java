package com.adam.adventure.event;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

//Potentially have the event bus as a background thread
public class EventBus {
    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

    private final Multimap<Class<? extends Event>, InstanceAndMethod> eventTypeToSubscribers;

    public EventBus() {
        this.eventTypeToSubscribers = HashMultimap.create();
    }

    @SuppressWarnings("unchecked")
    public void register(final Object subscriber) {
        final Set<Method> susbscribedMethods = Arrays.stream(subscriber.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(EventSubscribe.class))
                .collect(Collectors.toSet());

        for (final Method subscribedMethod : susbscribedMethods) {
            final int parameterCount = subscribedMethod.getParameterCount();
            if (parameterCount != 1) {
                throw new IllegalArgumentException("Method: " + subscribedMethod.getName() +
                        " annotated with @EventSubscribe should have only one parameter");
            }

            final Class<?> parameterType = subscribedMethod.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(parameterType)) {
                throw new IllegalArgumentException("Method " + subscribedMethod
                        + " annotated with @EventSubscribe does not have a method taking an event as an argument");
            }

            final InstanceAndMethod instanceAndMethod = new InstanceAndMethod(subscriber, subscribedMethod);
            eventTypeToSubscribers.put((Class<? extends Event>) parameterType, instanceAndMethod);
        }
    }

    public void publishEvent(final Event event) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Publishing new event: {}", event.getClass());
        }

        broadcastForExactListeners(event);
        //For now only supports one level, could use recursion in the future if needed
        broadcastForSuperclassListeners(event);
    }

    @SuppressWarnings("unchecked")
    private void broadcastForSuperclassListeners(final Event event) {
        final Class<?> superclass = event.getClass().getSuperclass();
        if (Event.class.isAssignableFrom(superclass)) {
            final Class<? extends Event> eventSuperclass = (Class<? extends Event>) superclass;
            final Collection<InstanceAndMethod> subscribedInstances = eventTypeToSubscribers.get(eventSuperclass);
            subscribedInstances.forEach(subscribedInstance -> subscribedInstance.invoke(event));
        }
    }

    private void broadcastForExactListeners(final Event event) {
        final Collection<InstanceAndMethod> subscribedInstances = eventTypeToSubscribers.get(event.getClass());
        if (subscribedInstances != null) {
            subscribedInstances.forEach(subscribedInstance -> subscribedInstance.invoke(event));
        }
    }

    private static class InstanceAndMethod {
        private final Object instance;
        private final Method method;

        private InstanceAndMethod(final Object instance, final Method method) {
            this.instance = instance;
            this.method = method;
        }

        private void invoke(final Event event) {
            try {
                method.setAccessible(true);
                method.invoke(instance, event);
                method.setAccessible(false);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final InstanceAndMethod that = (InstanceAndMethod) o;
            return Objects.equal(instance, that.instance) &&
                    Objects.equal(method, that.method);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(instance, method);
        }
    }
}

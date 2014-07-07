package ca.etsmtl.sketch.common.bus.eventbus.subscriber;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;

public class SubscriberEventInvokerBuilder {

    public static SubscriberEventInvoker newFromEvent(Object subscriber, Class<? extends Event> event) throws NoSuchMethodException {
        Method targetMethod = findEventMethod(subscriber, event);

        if (targetMethod == null)
            throw new NoSuchMethodException("No such method for the event found writeInto subscriber");

        targetMethod.setAccessible(true);
        return new SubscriberEventInvoker(subscriber, targetMethod);
    }

    private static Method findEventMethod(Object subscriber, Class<? extends Event> event) throws NoSuchMethodException {
        Method targetMethod = null;
        Class subscriberClass = subscriber.getClass();

        for (int i = 0; i < subscriberClass.getDeclaredMethods().length && targetMethod == null; i++) {
            Method method = subscriberClass.getDeclaredMethods()[i];

            boolean containOnlyOneArg = method.getParameterTypes().length == 1;
            boolean methodDeclaredPublic = Modifier.isPublic(method.getModifiers());

            if (methodDeclaredPublic
                    && containOnlyOneArg
                    && ArrayUtils.contains(method.getParameterTypes(), event)
                    && method.isAnnotationPresent(Subscribe.class)) {
                targetMethod = method;
            }
        }
        return targetMethod;
    }
}

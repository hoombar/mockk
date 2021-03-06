package io.mockk.proxy.android;

import io.mockk.agent.MockKAgentLogger;
import io.mockk.agent.MockKInstantiatior;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class AndroidMockKInstantiator implements MockKInstantiatior {
    private final ObjenesisStd objenesis;
    private final Map<Class<?>, ObjectInstantiator<?>> instantiators = Collections.synchronizedMap(new WeakHashMap<Class<?>, ObjectInstantiator<?>>());

    public static MockKAgentLogger log = MockKAgentLogger.NO_OP;

    public AndroidMockKInstantiator() {
        objenesis = new ObjenesisStd(true);
    }

    @Override
    public <T> T instance(Class<T> clazz) {
        if (clazz.isInterface()) {
            clazz = (Class<T>) Proxy.getProxyClass(clazz.getClassLoader(), clazz);
        }

        log.trace("Creating new empty instance of " + clazz);
        ObjectInstantiator<?> inst = instantiators.get(clazz);
        if (inst == null) {
            inst = objenesis.getInstantiatorOf(clazz);
            instantiators.put(clazz, inst);
        }
        return clazz.cast(inst.newInstance());
    }
}

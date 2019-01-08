package com.adam.adventure.module;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedInterceptor implements MethodInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(TimedInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final long startTime = System.currentTimeMillis();

        Object returnValue = invocation.proceed();

        final long duration = System.currentTimeMillis() - startTime;
        LOG.info("Took: {}ms to execute: {}.{}", duration,
                invocation.getMethod().getDeclaringClass().getSimpleName(),
                invocation.getMethod().getName());

        return returnValue;
    }
}

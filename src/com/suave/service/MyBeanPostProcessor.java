package com.suave.service;

import com.suave.spring.BeanPostProcessor;
import com.suave.spring.annotations.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author suave
 * @date 2022-05-01 21:54
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (Objects.equals(beanName, "userService")) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println(method.getName() + " postProcessBeforeInitialization代理逻辑。。。。");
                    return method.invoke(bean, args);
                }
            });
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (Objects.equals(beanName, "userService")) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println(method.getName() + " postProcessAfterInitialization代理逻辑。。。。");
                    return method.invoke(bean, args);
                }
            });
        }
        return bean;
    }
}

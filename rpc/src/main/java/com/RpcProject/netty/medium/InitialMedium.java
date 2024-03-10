package com.RpcProject.netty.medium;

import com.RpcProject.netty.annotation.Remote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


import java.lang.reflect.Method;
import java.util.Map;

@Component
public class InitialMedium implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Remote.class)) {
            Method[] methods = bean.getClass().getDeclaredMethods();
            for(Method method : methods){
                String key = bean.getClass().getInterfaces()[0].getName()+"."+method.getName();
                Map<String,BeanMethod> beanMethodMap = Media.beanMethodMap;
                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(bean);
                beanMethod.setMethod(method);
                beanMethodMap.put(key,beanMethod);
            }
        }
        return bean;
    }
}

package com.RpcProject.client.proxy;

import com.RpcProject.client.annotation.RemoteInvoke;
import com.RpcProject.client.core.TcpClient;
import com.RpcProject.client.param.ClientRequest;
import com.RpcProject.client.param.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class InvokeProxy implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(RemoteInvoke.class)){
                field.setAccessible(true);
                final Map<Method,Class> methodClassMap = new HashMap<>();
                putMethodClass(methodClassMap,field);
                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new Class[]{field.getType()});
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        //use netty client to call server
                        ClientRequest request = new ClientRequest();
                        request.setCommand(methodClassMap.get(method).getName()+"."+method.getName());
                        request.setContent(args[0]);
                        Response response = TcpClient.send(request);
                        return response;
                    }
                });
                try {
                    field.set(bean,enhancer.create());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }


    //Put all methods and property interface types of the attribute into a map.

    private void putMethodClass(Map<Method, Class> methodClassMap, Field field) {
        Method[] methods = field.getType().getDeclaredMethods();
        for(Method method : methods){
            methodClassMap.put(method,field.getType());
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}

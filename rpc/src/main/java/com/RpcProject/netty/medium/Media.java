package com.RpcProject.netty.medium;

import com.RpcProject.netty.util.Response;
import com.RpcProject.netty.handler.param.ServerRequest;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Media {
    public static Map<String,BeanMethod> beanMethodMap;
    static {
        beanMethodMap = new HashMap<String,BeanMethod>();
    }
    private static Media media = null;
    private Media(){

    }
    public static Media newInstance(){
        if(media==null){
            media=new Media();
        }
        return media;
    }

    public Response process(ServerRequest serverRequest){
        Response result = null;
        try {
            String command = serverRequest.getCommand();
            BeanMethod beanMethod = beanMethodMap.get(command);
            if(beanMethod==null){
                return null;
            }
            Object bean = beanMethod.getBean();

            Method method = beanMethod.getMethod();

            Class paramType = method.getParameterTypes()[0];

            Object content = serverRequest.getContent();

            Object args = JSONObject.parseObject(JSONObject.toJSONString(content),paramType);

            result = (Response) method.invoke(bean,args);
            result.setId(serverRequest.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

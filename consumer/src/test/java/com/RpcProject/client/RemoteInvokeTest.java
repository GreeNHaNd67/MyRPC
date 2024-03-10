
package com.RpcProject.client;

import com.RpcProject.client.annotation.RemoteInvoke;
import com.RpcProject.client.param.Response;
import com.RpcProject.user.bean.User;
import com.RpcProject.user.remote.UserRemote;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RemoteInvokeTest.class)
@ComponentScan("com.RpcProject")
public class RemoteInvokeTest {
    @RemoteInvoke
    public static UserRemote userRemote;
    public static User user;

    static{
        user = new User();
        user.setId(1000);
        user.setName("Ryan");
    }
    @Test
    public void testSaveUser(){
        User user = new User();
        user.setId(1000);
        user.setName("Ryan");
        userRemote.saveUser(user);
		Long start = System.currentTimeMillis();
		for(int i=1;i<100;i++){
			userRemote.saveUser(user);
		}
		Long end = System.currentTimeMillis();
		long count = end-start;
		System.out.println("Total time:"+count/1000+"second");

    }
}



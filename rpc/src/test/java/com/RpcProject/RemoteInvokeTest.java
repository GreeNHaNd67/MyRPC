/*
package com.RpcProject;

import com.RpcProject.netty.annotation.RemoteInvoke;
import com.RpcProject.user.bean.User;
import com.RpcProject.user.remote.UserRemote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RemoteInvokeTest.class)
@ComponentScan("com.RpcProject")
public class RemoteInvokeTest {
    @RemoteInvoke
    private UserRemote userRemote;

    @Test
    public void testSaveUser(){
        User user = new User();
        user.setId(1);
        user.setName("Ryan");
        userRemote.saveUser(user);
    }

    @Test
    public void testSaveUserList(){
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1);
        user.setName("Ryan");
        users.add(user);
        userRemote.saveUserList(users);
    }
}

 */

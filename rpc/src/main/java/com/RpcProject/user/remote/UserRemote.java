package com.RpcProject.user.remote;

import com.RpcProject.netty.util.Response;
import com.RpcProject.user.bean.User;

import java.util.List;

public interface UserRemote {
    public Response saveUser(User user);

    public Response saveUserList(List<User> userList);

}

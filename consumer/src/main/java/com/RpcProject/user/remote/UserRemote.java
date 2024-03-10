package com.RpcProject.user.remote;


import com.RpcProject.client.param.Response;
import com.RpcProject.user.bean.User;

import java.util.List;

public interface UserRemote {
    public Response saveUser(User user);

    public Response saveUserList(List<User> userList);

}

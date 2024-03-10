package com.RpcProject.user.remote;

import com.RpcProject.netty.annotation.Remote;
import com.RpcProject.netty.util.Response;
import com.RpcProject.netty.util.ResponseUtil;
import com.RpcProject.user.bean.User;
import com.RpcProject.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;

@Remote
public class UserRemoteImpl implements UserRemote{
    @Resource
    private UserService userService;
    public Response saveUser(User user){
        userService.save(user);
        return ResponseUtil.createSuccessResult(user);
    }

    public Response saveUserList(List<User> userList){
        userService.saveList(userList);
        return ResponseUtil.createSuccessResult(userList);
    }
}

package com.RpcProject.netty.handler;

import com.RpcProject.netty.client.DefaultFuture;
import com.RpcProject.netty.util.Response;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //System.out.println(msg.toString());
        if(msg.toString().equals("ping")){
            ctx.channel().writeAndFlush("ping\r\n");
            return;
        }
        //ctx.channel().attr(AttributeKey.valueOf("netty")).set(msg);
        Response response = JSONObject.parseObject(msg.toString(), Response.class);
        DefaultFuture.receive(response);
        //ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}

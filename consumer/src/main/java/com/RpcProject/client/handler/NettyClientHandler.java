package com.RpcProject.client.handler;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.RpcProject.client.core.DefaultFuture;
import com.RpcProject.client.param.Response;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Executor exec = Executors.newFixedThreadPool(10);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Object m = msg;
        if(msg.toString().equals("ping")){
            System.out.println("Received a read-write idle ping, sending a pong to the server");
            ctx.channel().writeAndFlush("pong\r\n");
        }

        exec.execute(new Runnable() {

            public void run() {
                Response response = JSONObject.parseObject(m.toString(), Response.class);
                //System.out.println("SimpleClientHandler Response:"+JSONObject.toJSONString(response));
                DefaultFuture.receive(response);
            }
        });
    }
}

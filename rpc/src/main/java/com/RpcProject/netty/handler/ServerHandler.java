package com.RpcProject.netty.handler;

import com.RpcProject.netty.handler.param.ServerRequest;
import com.RpcProject.netty.medium.Media;
import com.RpcProject.netty.util.Response;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Executor exec = Executors.newFixedThreadPool(10);
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        System.out.println("服务器Handler:"+msg.toString());
        exec.execute(new Runnable() {

            @Override
            public void run() {
                ServerRequest serverRequest = JSONObject.parseObject(msg.toString(), ServerRequest.class);
                System.out.println(serverRequest.getCommand());
                Media media = Media.newInstance();

                Response response = media.process(serverRequest);

                ctx.channel().writeAndFlush(JSONObject.toJSONString(response)+"\r\n");
            }
        });

    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state().equals(IdleState.READER_IDLE)){
                System.out.println("Reader_Idle ==");
                ctx.channel().close();
            }
            if(event.state().equals(IdleState.WRITER_IDLE)){
                System.out.println("Write_Idle ==");
            }
            else if(event.state().equals(IdleState.ALL_IDLE)){
                System.out.println("All_Idle");
                ctx.channel().writeAndFlush("ping\r\n");
            }
        }
    }
}

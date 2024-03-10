package com.RpcProject.client.core;

import com.RpcProject.client.handler.NettyClientHandler;
import com.RpcProject.client.param.ClientRequest;
import com.RpcProject.client.param.Response;
import com.RpcProject.client.zk.ZookeeperFactory;
import com.RpcProject.constant.Constants;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import java.util.List;

public class TcpClient {
    //
    static final Bootstrap b = new Bootstrap();
    static ChannelFuture f = null;
    static {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new NettyClientHandler());
                ch.pipeline().addLast(new StringEncoder());
            }
        });

        CuratorFramework client = ZookeeperFactory.create();
        String host = "localhost";
        int port = 8088;
        try {
            List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_PATH);
            //Add ZooKeeper listener to monitor server changes
            CuratorWatcher watcher = new ServerWatcher();

            client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);

            for(String serverPath : serverPaths){
                String[] str = serverPath.split("#");
                ChannelManager.realServerPath.add(str[0]+"#"+str[1]);
                ChannelFuture channelFuture = TcpClient.b.connect(str[0],Integer.valueOf(str[1]));
                ChannelManager.add(channelFuture);
            }
            if(ChannelManager.realServerPath.size()>0){
                String[] hostAndPort = ChannelManager.realServerPath.toArray()[0].toString().split("#");
                host = hostAndPort[0];
                port = Integer.valueOf(hostAndPort[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        try {
//            f = b.connect(host, port).sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    //send data
    public static Response send(ClientRequest request){
        f = ChannelManager.get(ChannelManager.position);
        f.channel().writeAndFlush(JSONObject.toJSONString(request)+"\r\n");
        //f.channel().writeAndFlush("\r\n");
        DefaultFuture defaultFuture = new DefaultFuture(request);
        Long timeOut = 60l;
//		return future.get(timeOut);
        return defaultFuture.get(timeOut);
    }

}

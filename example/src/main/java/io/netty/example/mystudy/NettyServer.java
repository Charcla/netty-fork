package io.netty.example.mystudy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss=new NioEventLoopGroup(1,new DefaultThreadFactory("nettyServerBoss"));
        NioEventLoopGroup work=new NioEventLoopGroup(new DefaultThreadFactory("nettyServerWork"));

        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,work);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(4096,0,4,0,4));
                    pipeline.addLast(new LengthFieldPrepender(4));
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            log.info("服务端收到请求为：{}",msg);
                            ctx.writeAndFlush(msg+msg);
                            log.info("服务端返回客户端为：{}",msg+msg);
                        }

                    });
                }
            });
            Channel ch = serverBootstrap.bind(9999).sync().channel();
            ch.closeFuture().sync();
        }catch (Exception e){
            log.error("netty server启动失败:{}",e);
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}

package io.netty.example.mystudy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
public class NettyClient {


    public static void main(String[] args) {

        StringBuffer q=new StringBuffer();
        StringBuffer w=new StringBuffer();
        StringBuffer e=new StringBuffer();
        StringBuffer r=new StringBuffer();
        StringBuffer t=new StringBuffer();
        StringBuffer y=new StringBuffer();
        for(int i=1;i<=20;i++){q.append("q");}
        for(int i=1;i<=50;i++){w.append("w");}
        for(int i=1;i<=100;i++){e.append("e");}
        for(int i=1;i<=150;i++){r.append("r");}
        for(int i=1;i<=200;i++){t.append("t");}
        for(int i=1;i<=3000;i++){y.append("y");}

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group=new NioEventLoopGroup(1);

        final ChannelHandlerContext[] context = new ChannelHandlerContext[1];
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.info("客户端建立连接");

                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024,0,4,0,4));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        pipeline.addLast(new StringEncoder(Charset.defaultCharset()));
                        pipeline.addLast(new StringDecoder(Charset.defaultCharset()));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                                for (int i=1;i<=1;i++){
//                                    ctx.writeAndFlush("abcdefqq11");
//                                }
                                context[0] =ctx;
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("收到服务端返回为：{}",((String)msg));
                            }
                        });

                    }
                });

        new Thread(()->{
            Scanner scanner=new Scanner(System.in);
            while (true){
                String line=scanner.nextLine();
                //发送20个
                if("q".equals(line)){
                    context[0].writeAndFlush(q.toString());
                }else if("w".equals(line)){ //发送50个
                    context[0].writeAndFlush(w.toString());
                }else if("e".equals(line)){ //发送100个
                    context[0].writeAndFlush(e.toString());
                }else if("r".equals(line)){ //发送150个
                    context[0].writeAndFlush(r.toString());
                }else if("t".equals(line)){ //发送200个
                    context[0].writeAndFlush(t.toString());
                }else if("y".equals(line)){
                    context[0].writeAndFlush(y.toString());
                }
                else {
                    context[0].writeAndFlush("qazwsxedcr");
                }
            }
        },"input").start();

        try{
            Channel channel=bootstrap.connect(new InetSocketAddress("localhost",9999)).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}

package io.netty.example.mystudy;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

@Slf4j
public class SocketClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9999);
        OutputStream outputStream = socket.getOutputStream();

        log.info("开始发送");
        String body="0005abcdef";
        //先发送报文长度，固定4字节
//        byte[] headBytes=new byte[4];
//        body.getBytes(Charset.defaultCharset());
//        outputStream.write();
        outputStream.write(body.getBytes(Charset.defaultCharset()));
        outputStream.flush();
        log.info("准备接受");
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        byte[] bytes=new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len=inputStream.read(bytes))!=-1){
            sb.append(new String(bytes,0,len,Charset.defaultCharset()));
        }

        log.info("接受完成，收到返回为：{}",sb);


        outputStream.close();
        inputStream.close();
        socket.close();
    }
}

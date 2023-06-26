package hanteen.web.pro.service.user.impl;

import static hanteen.web.pro.service.user.impl.TcpChatServerTest.online;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-06-06
 */
@SpringBootTest
public class TcpChatServerTest {

    public static ArrayList<Socket> online = new  ArrayList<Socket>();

    public static void main(String[] args) {
        //1、启动服务器，绑定端口号
        try (ServerSocket server = new ServerSocket(8989)) {

            //2、接收n多的客户端同时连接
            while (true) {
                Socket socket = server.accept(); //阻塞式的方法

                online.add(socket);//把新连接的客户端添加到online列表中

                //主要负责获取当前socket中的数据，并分发给当前聊天室的所有的客户端。
                MessageHandler mh = new MessageHandler(socket);
                mh.start();//
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MessageHandler extends Thread{
    private final Socket socket;
    private String ip;

    public MessageHandler(Socket socket) {
        super();
        this.socket = socket;
    }

    public void run(){
        try (Socket curr = socket;
                //(1)接收该客户端的发送的消息
                InputStream input = curr.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(reader);) {
            ip = curr.getInetAddress().getHostAddress();

            //插入：给其他客户端转发“我上线了”
            sendToOther(ip + "上线了", curr);

            String str;
            while((str = br.readLine())!=null){
                //(2)给其他在线客户端转发
                sendToOther(ip + ":" + str, curr);
            }

            sendToOther(ip + "下线了", curr);
        } catch (IOException e) {
            sendToOther(ip + "掉线了", null);
        } finally {
            //从在线人员中移除我
            online.remove(socket);
        }
    }

    //封装一个方法：给其他客户端转发xxx消息
    public void sendToOther(String message, Socket socket) {
        try {
            //遍历所有的在线客户端，一一转发
            for (Socket on : online) {
                if (on == socket) { //不用给自己发
                    continue;
                }
                OutputStream every = on.getOutputStream();
                //为什么用PrintStream？目的用它的println方法，按行打印
                PrintStream ps = new PrintStream(every);

                ps.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

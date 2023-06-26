package hanteen.web.pro.service.user.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-06-05
 */
@SpringBootTest
public class TcpChatClientTest {

    public static void main(String[] args) {
        try (//1、连接服务器
                Socket socket = new Socket("127.0.0.1",8989);) {

            //2、开启两个线程
            //(1)一个线程负责看别人聊，即接收服务器转发的消息
            Receive receive = new Receive(socket);
            receive.start();

            //(2)一个线程负责发送自己的话
            Send send = new Send(socket);
            send.start();

            send.join();//发送线程停止后，下线，close socket
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Send extends Thread {
    private final Socket socket;

    public Send(Socket socket) {
        super();
        this.socket = socket;
    }

    public void run(){
        try (OutputStream outputStream = socket.getOutputStream();
                //按行打印
                PrintStream ps = new PrintStream(outputStream);
                Scanner input = new Scanner(System.in);) {
            //从键盘不断的输入自己的话，给服务器发送，由服务器给其他人转发
            while(true){
                String str = input.nextLine(); //阻塞式的方法
                System.out.println("我：" + str);
                if("bye".equals(str)){
                    break;
                }
                ps.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
class Receive extends Thread {
    private final Socket socket;

    public Receive(Socket socket) {
        super();
        this.socket = socket;
    }

    public void run(){
        try (InputStream inputStream = socket.getInputStream();) {
            Scanner input = new Scanner(inputStream);
            while(input.hasNextLine()){
                String line = input.nextLine();
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
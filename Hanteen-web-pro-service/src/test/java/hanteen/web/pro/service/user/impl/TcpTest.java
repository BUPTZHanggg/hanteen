package hanteen.web.pro.service.user.impl;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-05-09
 */
@SpringBootTest
public class TcpTest {

    @Test
    public void client() {
        int port = 8989;
        InetAddress inetAddress = null; //声明对方的ip地址
        try {
            inetAddress = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try (Socket socket = new Socket(inetAddress, port);
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();) {
            //1. 创建一个Socket
            //2. 发送数据
            os.write("你好，我是客户端，请多多关照".getBytes());
            //客户端表明不再继续发送数据
//            socket.shutdownOutput();

            //3. 接收来着于服务器端的数据
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer1 = new byte[5];
            int len1;
            while((len1 = is.read(buffer1)) != -1){
                baos.write(buffer1,0,len1);
            }
            System.out.println(baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void server() {
        int port = 8989;
        try (ServerSocket serverSocket = new ServerSocket(port);
                //阻塞式的方法
                Socket socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                //内部维护了一个byte[]
                ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            //1. 创建一个ServerSocket

            //2. 调用accept()，接收客户端的Socket
            System.out.println("服务器端已开启");
            System.out.println("收到了来自于" + socket.getInetAddress().getHostAddress() + "的连接");
            byte[] buffer = new byte[3];
            int len;
            while ((len = is.read(buffer)) != -1) {
                //错误的，buffer长度小时可能会出现乱码。
//                String str = new String(buffer, 0, len);
//                System.out.print(str);
                System.out.println(len + "：" + System.currentTimeMillis());
                baos.write(buffer, 0, len);
            }
            System.out.println(baos);
            System.out.println("数据接收完毕");

            //3.返给客户端的信息
            os.write("收到数据了！".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
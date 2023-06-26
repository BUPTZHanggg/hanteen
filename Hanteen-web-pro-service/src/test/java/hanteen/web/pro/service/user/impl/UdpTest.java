package hanteen.web.pro.service.user.impl;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-06-16
 */
@SpringBootTest
public class UdpTest {

    //发送端
    @Test
    public void sender() {
        try (//1. 创建DatagramSocket的实例
                DatagramSocket ds = new DatagramSocket();) {

            //2. 将数据、目的地的ip，目的地的端口号都封装在DatagramPacket数据报中
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
            int port = 9090;
            byte[] bytes = "我是发送端".getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet =  new DatagramPacket(bytes,0,bytes.length,inetAddress,port);

            //发送数据
            ds.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //接收端
    @Test
    public void receiver() {
        try (//1. 创建DatagramSocket的实例
                DatagramSocket ds = new DatagramSocket(9090);) {

            //2. 创建数据报的对象，用于接收发送端发送过来的数据
            byte[] buffer = new byte[1024 * 64];
            DatagramPacket packet = new DatagramPacket(buffer,0,buffer.length);

            //3. 接收数据
            ds.receive(packet);

            //4.获取数据，并打印到控制台上
            String str = new String(packet.getData(),0,packet.getLength());
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

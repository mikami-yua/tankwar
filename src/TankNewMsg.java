import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 *坦克新加入的消息
 */
public class TankNewMsg {
    Tank tank;

    public TankNewMsg(Tank tank) {
        this.tank = tank;
    }

    public void send(DatagramSocket ds,String IP,int udpPort) {
        //通过DatagramSocket往外传
        /*
        怎么把一个信息发送出去：
            1.首先转换为字节数组,new出来ByteArrayOutputStream之后内存里就有了字节数组，默认32字节。可以自动增长
            2.把tank相关信息写入
            3.把字节数组封装为datagrampacket
                bytesArr.toByteArray()把写好数据的字节数组转换
         */
        ByteArrayOutputStream bytesArr=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bytesArr);//为了好写
        try {
            dos.writeInt(tank.id);
            dos.writeInt(tank.x);
            dos.writeInt(tank.y);
            dos.writeInt(tank.dir.ordinal());//获得方向的下标
            dos.writeBoolean(tank.good);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buf=bytesArr.toByteArray();
        DatagramPacket dp=new DatagramPacket(buf,buf.length,new InetSocketAddress(IP,udpPort));
        try {
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 *坦克新加入的消息
 */
public class TankNewMsg implements Msg{
    int msgType=Msg.TANK_NEW_MSG;
    Tank tank;
    TankClient tc;

    public TankNewMsg() {
    }

    public TankNewMsg(TankClient tc) {
        this.tc = tc;
    }

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
            dos.writeInt(msgType);
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

    public void parse(DataInputStream dis) {
        try {
            int id=dis.readInt();
            if(tc.myTank.id==id){//说明消息是自己发出去的，不做任何响应
                return;
            }


            int x=dis.readInt();
            int y=dis.readInt();
            Direction dir=Direction.values()[dis.readInt()];
            boolean good=dis.readBoolean();
            System.out.println("id:"+id+"-x:"+x+"-y:"+y+"-dir:"+dir+"-good:"+good);
            boolean exist=false;
            for(int i=0;i<tc.tanks.size();i++){
                Tank t=tc.tanks.get(i);
                if(t.id==id){
                    exist=true;
                    break;
                }
            }
            if(!exist) {
                TankNewMsg tnMsg=new TankNewMsg(tc.myTank);
                tc.nc.send(tnMsg);
                Tank t = new Tank(x, y, good, dir, tc);
                t.id = id;
                tc.tanks.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

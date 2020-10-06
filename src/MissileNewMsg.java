import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileNewMsg implements Msg{
    int msgType=Msg.MISSILE_NEW_MSG;
    TankClient tc;
    Missile m;

    public MissileNewMsg(Missile m) {
        this.m = m;
    }

    public MissileNewMsg(TankClient tc) {
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int udpPort) {
        ByteArrayOutputStream bytesArr=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bytesArr);//为了好写
        try {
            dos.writeInt(msgType);
            dos.writeInt(m.tankId);
            dos.writeInt(m.id);
            dos.writeInt(m.x);
            dos.writeInt(m.y);
            dos.writeInt(m.dir.ordinal());//获得方向的下标
            dos.writeBoolean(m.good);
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

    @Override
    public void parse(DataInputStream dis) {
        try {
            int tankId=dis.readInt();
            if(tankId==tc.myTank.id)
                return;
            int id=dis.readInt();
            int x=dis.readInt();
            int y=dis.readInt();
            Direction dir=Direction.values()[dis.readInt()];
            boolean good=dis.readBoolean();
            Missile m=new Missile(tankId,x,y,good,dir,tc);
            m.id=id;
            tc.missiles.add(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

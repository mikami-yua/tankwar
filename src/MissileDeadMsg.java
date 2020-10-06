import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeadMsg implements Msg{
    int msgType=Msg.MISSILE_DEAD_MSG;
    TankClient tc;
    int tankId;
    int id;

    public MissileDeadMsg(int tankId, int id) {
        this.tankId = tankId;
        this.id = id;
    }

    public MissileDeadMsg(TankClient tc) {
        this.tc = tc;
    }



    @Override
    public void send(DatagramSocket ds, String IP, int udpPort) {
        ByteArrayOutputStream bytesArr=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bytesArr);//为了好写
        try {
            dos.writeInt(msgType);
            dos.writeInt(tankId);
            dos.writeInt(id);

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
            /*if(tc.myTank.id==tankId){//说明消息是自己发出去的，不做任何响应
                return;
            }*/
            int id=dis.readInt();
            for(int i=0;i<tc.missiles.size();i++){
                Missile m=tc.missiles.get(i);
                if(m.tankId==tankId && m.id==id){
                    m.live=false;
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

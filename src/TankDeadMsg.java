import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeadMsg implements Msg{
    int msgType=Msg.TANK_DEAD_MSG;
    TankClient tc;
    int id;

    public TankDeadMsg(int id) {
        this.id = id;
    }

    public TankDeadMsg(TankClient tc) {
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int udpPort) {
        ByteArrayOutputStream bytesArr=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bytesArr);//为了好写
        try {
            dos.writeInt(msgType);
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
            int id=dis.readInt();
            if(tc.myTank.id==id){//说明消息是自己发出去的，不做任何响应
                return;
            }
            for(int i=0;i<tc.tanks.size();i++){
                Tank t=tc.tanks.get(i);
                if(t.id==id){
                    t.setLive(false);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

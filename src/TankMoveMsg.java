import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg{

    /*
    只要换方向就应该发送消息
     */
    int msgType=Msg.TANK_MOVE_MSG;
    int id;
    int x,y;
    Direction dir;
    TankClient tc;

    public TankMoveMsg(int id, Direction dir,int x,int y) {
        this.id = id;
        this.dir = dir;
        this.x=x;
        this.y=y;
    }

    public TankMoveMsg(TankClient tc) {
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int udpPort) {
        ByteArrayOutputStream bytesArr=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bytesArr);//为了好写
        try {
            dos.writeInt(msgType);
            dos.writeInt(id);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(dir.ordinal());//获得方向的下标
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
            int x=dis.readInt();
            int y=dis.readInt();
            Direction dir=Direction.values()[dis.readInt()];
            boolean exist=false;
            for(int i=0;i<tc.tanks.size();i++){
                Tank t=tc.tanks.get(i);
                if(t.id==id){
                    t.x=x;
                    t.y=y;
                    t.dir=dir;
                    exist=true;
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

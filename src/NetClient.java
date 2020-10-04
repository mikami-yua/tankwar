import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class NetClient {
    //定义自己的UPD端口号，将需要接受消息,不能写死，可能会有多个client
    private static int UDP_PORT_START=2223;
    private int udpPort;
    TankClient tc;
    DatagramSocket ds=null;

    public NetClient(TankClient tc){
        udpPort=UDP_PORT_START++;//当两个线程同时++，产生冲突。但此时并不会出现这种情况
        this.tc=tc;
        try {
            ds=new DatagramSocket(udpPort);//码头
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * 连接到服务器
     * @param IP 服务器IP
     * @param port 服务器端口
     */
    public void connect(String IP,int port){
        Socket s=null;
        try {
            s=new Socket(IP,port);//这样就连上了

            //把自己的UPD端口号发送给服务器
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            dos.writeInt(udpPort);
            DataInputStream dis=new DataInputStream(s.getInputStream());
            int id=dis.readInt();
            tc.myTank.id=id;
            System.out.println("Connected To Server! And ID is : "+id);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (s != null) {
                try {
                    s.close();//tcp连接建立后，socket使命已完成,需要在final中关闭，如果传端口时出错就关不掉了
                    s=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //1.连接上以后new一个加入消息
        TankNewMsg msg=new TankNewMsg(tc.myTank);
        //2.需要把msg send出去。把相关信息全部放进字节数组，全部封装为datagrampacket。再通过datagramsocket把这个packet发送出去
        send(msg);


    }
    public void send(TankNewMsg msg){
        //需要知道msg里什么信息，再依次发送出去。需要知道一个类的具体信息，不符合面向对象的封装性
        msg.send(ds,"127.0.0.1",TankServer.UDP_PORT);//让一个对象自己做自己的事，符合面相对象

    }
}

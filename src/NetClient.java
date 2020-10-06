import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class NetClient {
    //定义自己的UPD端口号，将需要接受消息,不能写死，可能会有多个client
    private int udpPort;
    TankClient tc;
    DatagramSocket ds=null;

    public NetClient(TankClient tc){
        this.tc=tc;

    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    /**
     * 连接到服务器
     * @param IP 服务器IP
     * @param port 服务器端口
     */
    public void connect(String IP,int port){
        try {
            ds=new DatagramSocket(udpPort);//码头
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Socket s=null;
        try {
            s=new Socket(IP,port);//这样就连上了

            //把自己的UPD端口号发送给服务器
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            dos.writeInt(udpPort);
            DataInputStream dis=new DataInputStream(s.getInputStream());
            int id=dis.readInt();
            tc.myTank.id=id;
            if(id%2==0) tc.myTank.good=false;
            else tc.myTank.good=true;
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

        new Thread(new UDPRecvThread()).start();
    }
    public void send(Msg msg){
        //需要知道msg里什么信息，再依次发送出去。需要知道一个类的具体信息，不符合面向对象的封装性
        msg.send(ds,"127.0.0.1",TankServer.UDP_PORT);//让一个对象自己做自己的事，符合面相对象

    }

    private class UDPRecvThread implements Runnable{

        byte[] buf=new byte[1024];

        @Override
        public void run() {
            while(true){
                while (ds !=null){
                    DatagramPacket dp=new DatagramPacket(buf,buf.length);//一个集装箱，真正装数据的是buf
                    try {
                    /*
                    receive是接货的码头，接到的数据放到集装箱中
                     */
                        ds.receive(dp);//通过服务的receive方法将接收到的数据存入到数据包中
                        System.out.println("One Package received From Server !");

                        /*
                        对接受到数据进行解析
                         */
                        parse(dp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 解析收到的UPD数据包,数据dp=new DatagramPacket(buf,buf.length);//一个集装箱，真正装数据的是buf
         * @param dp 收到的集装箱
         */
        private void parse(DatagramPacket dp) {
            ByteArrayInputStream bais=new ByteArrayInputStream(buf,0,dp.getLength());//一根管道怼到数组上
            //开始读
            DataInputStream dis=new DataInputStream(bais);
            int msgType= 0;
            try {
                msgType = dis.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Msg msg=null;
            switch (msgType){
                case Msg.TANK_NEW_MSG:

                    msg=new TankNewMsg(tc);
                    //一个对象自己最清楚自己，把管道交给他自己，自己分析
                    msg.parse(dis);
                    break;
                case Msg.TANK_MOVE_MSG:
                    msg=new TankMoveMsg(tc);
                    msg.parse(dis);
                    break;
                case Msg.MISSILE_NEW_MSG:
                    msg=new MissileNewMsg(NetClient.this.tc);
                    msg.parse(dis);
                    break;
                case Msg.TANK_DEAD_MSG:
                    msg=new TankDeadMsg(NetClient.this.tc);
                    msg.parse(dis);
                    break;
                case Msg.MISSILE_DEAD_MSG:
                    msg=new MissileDeadMsg(NetClient.this.tc);
                    break;
            }

        }
    }
}

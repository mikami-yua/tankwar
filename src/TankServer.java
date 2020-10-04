import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TankServer {
    private static int ID=100;//服务器不需要这个号码，需要把这个号码写会给客户端

    //定义tcp端口
    public static final int TCP_PORT=8888;
    public static final int UDP_PORT=6666;

    //对于Server来说应该保留一系列的客户端信息
    List<Client> clients=new ArrayList<>();

    public void start(){
        //启动UDP监听线程
        new Thread(new UDPThread()).start();


        ServerSocket ss=null;
        try {
            ss=new ServerSocket(TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            Socket s=null;
            try {
                s = ss.accept();


                //接受客户端的udp端口信息
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int udpPort = dis.readInt();
                String IP = s.getInetAddress().getHostAddress();//这个表示IP
                Client c = new Client(IP, udpPort);
                clients.add(c);
                System.out.println("A Client is Connected! Addr: " + s.getInetAddress() + ":" + s.getPort()+
                        "UDP_PORT"+udpPort);
                DataOutputStream dos=new DataOutputStream(s.getOutputStream());
                dos.writeInt(ID++);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (s != null) {
                    try {
                        s.close();
                        s=null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new TankServer().start();
    }

    /*
    保存每个client连接上来时的信息
    IP,port(转发的是udp的端口)
    client端也应该有一个udp的监听线程
     */
    private class Client{
        String IP;
        int udpPort;

        public Client(String IP, int udpPort) {
            this.IP = IP;
            this.udpPort = udpPort;
        }
    }

    private class UDPThread implements Runnable{

        byte[] buf =new byte[1024];


        /**
         * 启动一个udp的线程，不断的监听，接受客户端发送到数据并转发给其他的客户端
         */
        @Override
        public void run() {
            System.out.println("UDP Thread Started At Port : "+UDP_PORT);

            //new一个udp的socket
            DatagramSocket ds=null;
            try {
                ds=new DatagramSocket(UDP_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            //udp socket建立之后可以接受客户端传的数据
            while (ds !=null){
                DatagramPacket dp=new DatagramPacket(buf,buf.length);//一个集装箱，真正装数据的是buf
                try {
                    /*
                    receive是接货的码头，接到的数据放到集装箱中
                     */
                    ds.receive(dp);//通过服务的receive方法将接收到的数据存入到数据包中
                    System.out.println("One Package received !");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

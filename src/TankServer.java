import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TankServer {
    //定义tcp端口
    private static final int TCP_PORT=8888;

    public static void main(String[] args) {
        try {
            ServerSocket ss=new ServerSocket(TCP_PORT);
            while(true){
                Socket s=ss.accept();
                System.out.println("A Client is Connected! Addr: "+s.getInetAddress()+":"+s.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

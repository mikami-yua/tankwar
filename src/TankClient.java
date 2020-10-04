import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 这个类的作用是tank游戏的主窗口
 * @author Mr.Jia
 */
public class TankClient extends Frame {
    //将窗口大小定义为常量
    public static final int GAMR_WIDTH=800;
    public static final int GAMR_HEIGTH=600;
    //public static final int GAME_ENEMTNUM=10;


    Tank myTank=new Tank(50,50,true,Direction.STOP,this);
    Wall w1=new Wall(100,200,20,150,this);
    Wall w2=new Wall(300,100,300,20,this);
    Blood bb=new Blood();
    NetClient nc=new NetClient();//TankClient相当于一个大总管，网络相关的事交给netclient去做

    List<Missile> missiles=new ArrayList<>();
    List<Explode> explodes=new ArrayList<>();
    List<Tank> tanks=new ArrayList<>();

    //doublebuffer,调用repaint的时候不能直接调用paint方法。调用paint方法之前必须首先把所有东西画在背面，再更新屏幕
    //1.定义背后的图片
    Image offScreenImage=null;

    /**
     * 本方法显示tank图片
     */
    public void lauchFrame(){

        int tankNum=Integer.parseInt(PropertyMgr.getProperties("initTankCount"));
        //窗口显示前，添加若干tank



        //定义窗口出现的位置
        this.setLocation(400,300);//屏幕的左上角点，往右数400，往下数300
        //设置宽度,高度
        this.setSize(GAMR_WIDTH,GAMR_HEIGTH);

        //关闭窗口采用匿名类足够了
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);//正常退出
            }
        });

        //加入键盘的监听器
        this.addKeyListener(new KeyMonitor());

        //不让窗口改变大小
        this.setResizable(false);

        //改变标题栏的文字
        this.setTitle("TankWar");

        //设置背景色
        this.setBackground(Color.GRAY);

        this.setVisible(true);

        //坦克移动线程在窗口启动之后就可以开始
        new Thread(new PaintThread()).start();

    }

    /**
     * 重写paint方法画图
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        /*
        必要信息的显示
         */
        g.drawString("missile count: "+missiles.size(),10,50);//把字符串画在后两个参数的位置上
        g.drawString("now exploding count: "+explodes.size(),200,50);
        g.drawString("now tanks count: "+tanks.size(),500,50);
        g.drawString("my life: "+myTank.getLife(),10,150);



        for(int i=0;i<missiles.size();i++){
            Missile m=missiles.get(i);
            m.hitTanks(tanks);
            m.hitTank(myTank);
            m.hitWall(w1);
            m.hitWall(w2);
            m.draw(g);
        }
        for(int i=0;i<explodes.size();i++){
            Explode e=explodes.get(i);
            e.draw(g);
        }
        for(int i=0;i<tanks.size();i++){
            Tank t=tanks.get(i);
            t.collidesWithWall(w1);
            t.collidesWithWall(w2);
            t.collidesWithTanks(tanks);
            t.draw(g);
        }

        myTank.draw(g);//frame递给的画笔，再递给坦克
        myTank.eat(bb);
        w1.draw(g);
        w2.draw(g);
        bb.draw(g);
    }

    /**
     * update也会传递给一只画笔，这只画笔同样是paint里的
     * @param g 前面屏幕的画笔
     */
    @Override
    public void update(Graphics g) {
        //在外部定义不需要每次都创建对象
        if(offScreenImage==null){
            offScreenImage=this.createImage(GAMR_WIDTH,GAMR_HEIGTH);//正在虚拟图片和原来的图片一样大
        }
        //把所有的东西都画到这张图片上
        //在这种图片上画首先要拿到图片画笔
        Graphics gOffScreen=offScreenImage.getGraphics();//这个是背后图片的画笔

        /*
        重擦背景
         */
        Color c=gOffScreen.getColor();
        gOffScreen.setColor(Color.GRAY);
        gOffScreen.fillRect(0,0,GAMR_WIDTH,GAMR_HEIGTH);
        gOffScreen.setColor(c);//把颜色设置回去

        //用这个画笔把所有东西都画出来，不需要把paint方法重写一遍，调用即可
        paint(gOffScreen);
        //再把图片画到前面来
        g.drawImage(offScreenImage,0,0,null);//参数1要画哪张图片,参数2，3表示距离左上角的位置。观察者暂时不用

        /*
        闪烁现象缓解，但是形成了一条线，没有重擦背景
         */

    }

    //使用内部类重画坦克合适，这个重画的线程只为TankClient服务
    private class PaintThread implements Runnable{
        @Override
        public void run() {
            while (true){
                repaint();//调用frame类的repaint方法，repaint会调用paint方法。可以完成重画
                try {
                    Thread.sleep(50);//每50ms重画一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //定义一个对键盘的监听，使用外部类，内部类都可以，这里使用内部类，可以非常方便的访问内部的成员变量
    public class KeyMonitor extends KeyAdapter{
        /**
         * 按下什么键就向哪里走
         * @param e
         */
        @Override
        public void keyPressed(KeyEvent e) {
            myTank.keyPress(e);//把按下的键盘交给tank对象去处理
        }

        /**
         * 键盘抬起来
         * @param e
         */
        @Override
        public void keyReleased(KeyEvent e) {
           myTank.keyReleased(e);
        }
    }

    public static void main(String[] args) {
        TankClient tc=new TankClient();
        tc.lauchFrame();
    }


}

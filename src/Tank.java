import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class Tank {
    public static final int XSPEED=5;
    public static final int YSPEED=5;
    public static final int WIDTH=30;
    public static final int HEIGHT=30;

    private int x,y;//坐标
    private boolean bL=false,bU=false,bR=false,bD=false;//是否按下了朝四个方向的键
    private Direction dir=Direction.STOP;//默认起始状态不动
    private Direction ptDir=Direction.D;//默认炮筒方向向下
    private boolean good;
    private boolean live=true;
    private static Random r=new Random();//建立一个随机数产生器
    private int step=r.nextInt(16)+3;//初始最少移动3步，最多移动18步
    private int oldX,oldY;//记录上次的位置
    private int life=100;//满生命值是100
    private BloodBar bb=new BloodBar();

    TankClient tc;




    public Tank(int x, int y,boolean good) {
        this.x = x;
        this.y = y;
        this.oldX=x;
        this.oldY=y;
        this.good=good;
    }

    public Tank(int x,int y,boolean good,Direction dir ,TankClient tc){
        this(x,y,good);//调用另一个构造方法
        this.tc=tc;
        this.dir=dir;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isGood() {
        return good;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    /**
     * tank画出自己
     * @param g 传入的画笔
     */
    public void draw(Graphics g){
        if(!live) {
            /*
            将来还需要考虑到底是玩家good=true死了
            还是bot good=false死l
            此版本先不考虑bot的反击
             */
            if(!good){
                tc.tanks.remove(this);
            }
            return;
        }
        if(good) bb.draw(g);
        //需要一个前景色（默认是黑色）
        Color c=g.getColor();
        if(good)  g.setColor(Color.BLUE);//自己的坦克画成蓝色
        else g.setColor(Color.RED);
        g.fillOval(x,y,WIDTH,HEIGHT);

        //不改变原来的前景色，用的时候改变一下，用完了再设置回去
        g.setColor(c);//把原来的颜色设置回来

        /*
        根据炮筒的方向画直线
         */
        switch (ptDir) {
            case L:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x,y+Tank.HEIGHT/2);//画一条线（中心的点，左上角，右上角）
                break;
            case LD:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x,y+Tank.HEIGHT);
                break;
            case D:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH/2,y+Tank.HEIGHT);
                break;
            case R:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH,y+Tank.HEIGHT/2);
                break;
            case U:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH/2,y);
                break;
            case LU:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x,y);//坐上就是xy本身
                break;
            case RD:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH,y+Tank.HEIGHT);
                break;
            case RU:
                g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH,y);
                break;
        }


        move();
    }

    private void move(){
        //每move一下，记录xy的位置
        this.oldX=x;
        this.oldY=y;

        //根据当前的方向移动
        switch (dir){
            case L:
                x-=XSPEED;
                break;
            case LD:
                x-=XSPEED;
                y+=YSPEED;
                break;
            case D:
                y+=YSPEED;
                break;
            case R:
                x+=XSPEED;
                break;
            case U:
                y-=YSPEED;
                break;
            case LU:
                x-=XSPEED;
                y-=YSPEED;
                break;
            case RD:
                x+=XSPEED;
                y+=YSPEED;
                break;
            case RU:
                x+=XSPEED;
                y-=YSPEED;
                break;
            case STOP:
                break;
        }
        if(this.dir != Direction.STOP){//调整炮筒方向
            this.ptDir=this.dir;
        }

        if(x<0) x=0;
        if(y<30) y=30;
        if(x+Tank.WIDTH>TankClient.GAMR_WIDTH) x=TankClient.GAMR_WIDTH-Tank.WIDTH;
        if(y+Tank.HEIGHT>TankClient.GAMR_HEIGTH) y=TankClient.GAMR_HEIGTH-Tank.HEIGHT;

        /*
        每次move的时候就让bot tank改变方向
        使用随机数，随机产生方向
         */
        if(!good){
            Direction[] dirs=Direction.values();//转换为数组
            if(step==0){
                step=r.nextInt(16)+3;
                int rn=r.nextInt(dirs.length);//产生一个随机的整数，在8以内
                dir=dirs[rn];
            }
            step--;
            if(r.nextInt(30)>25) this.fire();
        }


    }

    public void keyPress(KeyEvent e){
        //1.获得键的虚拟码
        int key=e.getKeyCode();
        //2.拿虚拟码可keyevent里的常量进行比较
//            if(key==KeyEvent.VK_RIGHT){//如果朝右
//                x+=5;
//            }
        switch (key){
            //添加对crtl键的处理
            case KeyEvent.VK_F2:
                if(!this.live){
                    this.live=true;
                    this.life=100;
                }
                break;
            case KeyEvent.VK_LEFT:
                bL=true;
                break;
            case KeyEvent.VK_UP:
                bU=true;
                break;
            case KeyEvent.VK_RIGHT:
                bR=true;
                break;
            case KeyEvent.VK_DOWN:
                bD=true;
                break;
        }
        //按下键之后重新定位方向
        locateDirection();
    }

    /**
     * 朝那边抬起来，把那边设置为false
     * @param e
     */
    public void keyReleased(KeyEvent e) {

        int key=e.getKeyCode();
        switch (key){
            case KeyEvent.VK_CONTROL:
                fire();
                break;
            case KeyEvent.VK_LEFT:
                bL=false;
                break;
            case KeyEvent.VK_UP:
                bU=false;
                break;
            case KeyEvent.VK_RIGHT:
                bR=false;
                break;
            case KeyEvent.VK_DOWN:
                bD=false;
                break;
            case KeyEvent.VK_A:
                superFire();
                break;
        }
        //最后再重新定位
        locateDirection();
    }

    /**
     * 设置tank具体的方向
     */
    private void locateDirection(){
        if(bL && !bU && !bR && !bD) dir=Direction.L;
        else if(bL && bU && !bR && !bD) dir=Direction.LU;
        else if(bL && !bU && !bR && bD) dir=Direction.LD;
        else if(!bL && bU && !bR && !bD) dir=Direction.U;
        else if(!bL && bU && bR && !bD) dir=Direction.RU;
        else if(!bL && !bU && bR && !bD) dir=Direction.R;
        else if(!bL && !bU && bR && bD) dir=Direction.RD;
        else if(!bL && !bU && !bR && bD) dir=Direction.D;
        else dir=Direction.STOP;
    }

    /**
     *
     * @return 打出一发子弹，返回也该Missile对象
     */
    public Missile fire(){
        if(!live) return null;
        int x=this.x+Tank.WIDTH/2 -Missile.WIDTH/2;
        int y=this.y+Tank.HEIGHT/2-Missile.HEIGHT/2;
        Missile m=new Missile(x,y,good,ptDir,this.tc);//从tank的位置，向着tank的方向new一个子弹
        tc.missiles.add(m);
        return m;
    }
    public Missile fire(Direction dir){
        if(!live) return null;
        int x=this.x+Tank.WIDTH/2 -Missile.WIDTH/2;
        int y=this.y+Tank.HEIGHT/2-Missile.HEIGHT/2;
        Missile m=new Missile(x,y,good,dir,this.tc);//从tank的位置，向着tank的方向new一个子弹
        tc.missiles.add(m);
        return m;
    }


    /**
     *Rectangle英文矩形
     * @return 拿到一个矩形，这个矩形是tank的位置xy，和tank的大小
     */
    public Rectangle getRect(){
        return new Rectangle(x,y,WIDTH,HEIGHT);
    }


    /**
     * 撞墙之后停止，回到上一个位置
     * @param w 被撞的墙
     * @return 撞上了返回true
     */
    public boolean collidesWithWall(Wall w){
        if(this.live && this.getRect().intersects(w.getRect())){
            this.stayBack();
            return true;
        }
        return false;
    }

    private void stayBack(){
        x=oldX;
        y=oldY;
    }

    /**
     * 坦克与坦克相撞
     * @param tanks
     * @return
     */
    public boolean collidesWithTanks(List<Tank> tanks){
        for (int i=0;i<tanks.size();i++){
            Tank t=tanks.get(i);
            if(this != t){//不是同一辆坦克，检测相撞
                if(this.live && t.isLive() && this.getRect().intersects(t.getRect())){
                    this.stayBack();
                    t.stayBack();
                    return true;
                }
                return false;
            }
        }

        return false;
    }

    private void superFire(){
        Direction[] dirs=Direction.values();
        for(int i=0;i<8;i++){
            fire(dirs[i]);
        }
    }

    private class BloodBar{
        public void draw(Graphics g){
            Color c=g.getColor();
            g.setColor(Color.RED);
            g.drawRect(x,y-10,WIDTH,10);//画空心的,在tank上面显示，宽度是坦克的宽度，高度为10
            //接下来画里面的框
            int nowlife=WIDTH*life/100;
            g.fillRect(x,y-10,nowlife,10);
            g.setColor(c);
        }
    }

    public Boolean eat(Blood b){
        if(b.isLive() &&this.live && this.getRect().intersects(b.getRect())){
            this.life=100;
            b.setLive(false);
            return true;
        }
        return false;
    }
}

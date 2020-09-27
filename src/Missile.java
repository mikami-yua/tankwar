import java.awt.*;
import java.util.List;

public class Missile {
    public static final int XSPEED=10;
    public static final int YSPEED=10;
    public static final int WIDTH=10;
    public static final int HEIGHT=10;

    private TankClient tc;

    int x,y;//位置属性
    private boolean live=true;
    //有方向属性
    Tank.Direction dir;
    private boolean good;

    public boolean isLive() {
        return live;
    }


    public Missile(int x, int y, Tank.Direction dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }
    public Missile(int x, int y,boolean good,Tank.Direction dir,TankClient tc){
        this(x,y,dir);
        this.good=good;
        this.tc=tc;
    }
    

    //子弹需要把自己画出来
    public void draw(Graphics g){
        if(!live){
            tc.missiles.remove(this);
            return;
        }

        Color c=g.getColor();
        g.setColor(Color.BLACK);
        g.fillOval(x,y,WIDTH,HEIGHT);//子弹宽度10，高度10
        g.setColor(c);

        move();
    }

    private void move(){
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
        }
        /*
        出界炮弹就算死了
        */
        if(x<0 || y<0 || x>TankClient.GAMR_WIDTH || y>TankClient.GAMR_HEIGTH){
            live=false;
            tc.missiles.remove(this);
        }
    }

    /**
     *Rectangle英文矩形
     * @return 拿到一个矩形，这个矩形是子弹的位置xy，和子弹的大小
     */
    public Rectangle getReat(){
        return new Rectangle(x,y,WIDTH,HEIGHT);
    }

    /**
     * 判断是否击中坦克
     * @param t
     * @return
     */
    public boolean hitTank(Tank t){
        if(this.live &&this.getReat().intersects(t.getRect()) && t.isLive()
                            && this.good !=t.isGood()) {//intersects判断是否相交了
            t.setLive(false);
            this.live=false;
            //产生爆炸，产生在子弹位置
            Explode e=new Explode(this.x,this.y,this.tc);
            tc.explodes.add(e);
            return true;
        }
        return false;
    }

    public boolean hitTanks(List<Tank> tanks){
        for(int i=0;i<tanks.size();i++){
            if(hitTank(tanks.get(i))){
                return true;
            }
        }
        return false;
    }

    public boolean hitWall(Wall w){
        if(this.live && this.getReat().intersects(w.getRect())){
            this.live=false;
            return true;
        }
        return false;
    }


}

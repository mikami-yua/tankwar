import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Missile {
    public static final int XSPEED=10;
    public static final int YSPEED=10;
    public static final int WIDTH=10;
    public static final int HEIGHT=10;

    private TankClient tc;

    private static Toolkit tk= Toolkit.getDefaultToolkit(); //通过toolkit可以获得一些适合操作系统做的事。
    // 不同的操作系统提供的toolkit不一样.getDefaultToolkit()
    //拿到默认的工具包。通过工具包里的方法把硬盘的图片拽到内存
    private static Image[] missileimages =null;
    private static Map<String,Image> imgs=new HashMap<>();
    static {
        missileimages =new Image[] {
                //Explode.class.getClassLoader().getResource("images/0.gif")
                tk.getImage(Missile.class.getClassLoader().getResource("images/bulletL.gif")),
                tk.getImage(Missile.class.getClassLoader().getResource("images/missileLU.gif")),
                tk.getImage(Missile.class.getClassLoader().getResource("images/bulletU.gif")),
                tk.getImage(Missile.class.getClassLoader().getResource("images/missileRU.gif")),
                tk.getImage(Missile.class.getClassLoader().getResource("images/bulletR.gif")),
                tk.getImage(Missile.class.getClassLoader().getResource("images/missileRD.gif")),
                tk.getImage(Missile.class.getClassLoader().getResource("images/bulletD.gif")),
                tk.getImage(Missile.class.getClassLoader().getResource("images/missileLD.gif"))

        };

        imgs.put("L", missileimages[0]);
        imgs.put("LU", missileimages[1]);
        imgs.put("U", missileimages[2]);
        imgs.put("RU", missileimages[3]);
        imgs.put("R", missileimages[4]);
        imgs.put("RD", missileimages[5]);
        imgs.put("D", missileimages[6]);
        imgs.put("LD", missileimages[7]);
    }
    
    int x,y;//位置属性
    private boolean live=true;
    //有方向属性
    Direction dir;
    private boolean good;

    public boolean isLive() {
        return live;
    }


    public Missile(int x, int y, Direction dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }
    public Missile(int x, int y,boolean good,Direction dir,TankClient tc){
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
        /*
        Color c=g.getColor();
        if(good) g.setColor(Color.ORANGE);
        else g.setColor(Color.BLACK);
        g.fillOval(x,y,WIDTH,HEIGHT);//子弹宽度10，高度10
        g.setColor(c);
        *

         */
        switch (dir) {
            case L:
                g.drawImage(imgs.get("L"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x,y+Tank.HEIGHT/2);//画一条线（中心的点，左上角，右上角）
                break;
            case LD:
                g.drawImage(imgs.get("LD"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x,y+Tank.HEIGHT);
                break;
            case D:
                g.drawImage(imgs.get("D"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH/2,y+Tank.HEIGHT);
                break;
            case R:
                g.drawImage(imgs.get("R"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH,y+Tank.HEIGHT/2);
                break;
            case U:
                g.drawImage(imgs.get("U"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH/2,y);
                break;
            case LU:
                g.drawImage(imgs.get("LU"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x,y);//坐上就是xy本身
                break;
            case RD:
                g.drawImage(imgs.get("RD"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH,y+Tank.HEIGHT);
                break;
            case RU:
                g.drawImage(imgs.get("RU"),x,y,null);
                //g.drawLine(x+Tank.WIDTH/2,y+Tank.HEIGHT/2,x+Tank.WIDTH,y);
                break;
        }
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
            if(t.isGood()){
                t.setLife(t.getLife()-20);
                if(t.getLife()<=0) t.setLive(false);
            }else{
                t.setLive(false);
            }
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

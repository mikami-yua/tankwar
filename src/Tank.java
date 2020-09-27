import java.awt.*;
import java.awt.event.KeyEvent;

public class Tank {
    public static final int XSPEED=5;
    public static final int YSPEED=5;

    private int x,y;//坐标
    private boolean bL=false,bU=false,bR=false,bD=false;//是否按下了朝四个方向的键
    private Direction dir=Direction.STOP;//默认起始状态不动


    enum Direction{//枚举8个方向和停止
        L,LU,U,RU,R,RD,D,LD,STOP
    };

    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * tank画出自己
     * @param g 传入的画笔
     */
    public void draw(Graphics g){
        //需要一个前景色（默认是黑色）
        Color c=g.getColor();
        g.setColor(Color.BLUE);//自己的坦克画成蓝色
        g.fillOval(x,y,30,30);

        //不改变原来的前景色，用的时候改变一下，用完了再设置回去
        g.setColor(c);//把原来的颜色设置回来
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
            case STOP:
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
        else if(!bL && !bU && !bR && !bD) dir=Direction.STOP;
    }
}

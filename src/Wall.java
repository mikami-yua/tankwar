import java.awt.*;

public class Wall {
    int x,y,w,h;//位置
    TankClient tc;

    public Wall(int x, int y, int w, int h, TankClient tc) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.tc = tc;
    }

    public void draw(Graphics g){
        g.fillRect(x,y,w,h);
    }

    /**
     * 生产一个矩形，作为墙
     * @return
     */
    public Rectangle getRect(){
        return new Rectangle(x,y,w,h);
    }
}

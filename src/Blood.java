import java.awt.*;

public class Blood {
    public static int WIDTH=15;
    public static int HEIGHT=15;


    int x,y;
    int step=0;
    TankClient tc;
    private int[][] pos={
        {350,300},{360,300},{370,330},{380,350},{380,390},{370,390},{360,350},{350,300},
    };
    private boolean live=true;



    public Blood(){
        x=pos[0][0];
        y=pos[0][1];
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public void draw(Graphics g){
        if(!live) return;
        Color c=g.getColor();
        g.setColor(Color.MAGENTA);
        g.fillRect(x,y,WIDTH,HEIGHT);
        g.setColor(c);
        
        move();
    }

    private void move() {
        step++;
        step=step%8;
        x=pos[step][0];
        y=pos[step][1];
    }

    public Rectangle getRect(){
        return new Rectangle(x,y,WIDTH,HEIGHT);
    }
}

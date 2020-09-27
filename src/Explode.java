import java.awt.*;

public class Explode {
    int x,y;//爆炸出现的位置
    private boolean live=true;//爆炸是否存在

    private int[] diameter={3,5,7,9,11,13,15,17,19,21,23,27,39,13,11,9,7,5,3,1};//用直径不同的圆模拟爆炸
    private int step=0;//现在画到第几步了
    private TankClient tc;

    public Explode(int x, int y,TankClient tc) {
        this.x = x;
        this.y = y;
        this.tc=tc;
    }

    public void draw(Graphics g){
        if(!live) {
            tc.explodes.remove(this);
            return;
        }

        if(step==diameter.length){
            live=false;
            step=0;
            return;
        }

        Color c=g.getColor();
        g.setColor(Color.ORANGE);
        g.fillOval(x,y,diameter[step],diameter[step]);
        g.setColor(c);
        step++;
    }

}

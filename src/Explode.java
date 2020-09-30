import java.awt.Image;
import java.awt.Graphics;
import java.awt.Toolkit;

public class Explode {
    int x,y;//爆炸出现的位置
    private boolean live=true;//爆炸是否存在

    //private int[] diameter={3,5,7,9,11,13,15,17,19,21,23,27,39,13,11,9,7,5,3,1};//用直径不同的圆模拟爆炸
    //使用图片数组放图片,把图片从硬盘放到内存中

    private static Toolkit tk= Toolkit.getDefaultToolkit(); //通过toolkit可以获得一些适合操作系统做的事。
                                                            // 不同的操作系统提供的toolkit不一样.getDefaultToolkit()
                                                            //拿到默认的工具包。通过工具包里的方法把硬盘的图片拽到内存
    private static Image[] imgs={
            //Explode.class.getClassLoader().getResource("images/0.gif")
            tk.getImage("E:\\tankwar\\src\\images\\0.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\1.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\2.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\3.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\4.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\5.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\6.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\7.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\8.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\9.gif"),
            tk.getImage("E:\\tankwar\\src\\images\\10.gif")
    };
    private int step=0;//现在画到第几步了
    private TankClient tc;
    private static boolean init=false;//图片是否初始化，未初始化第一发炮弹打中没反应

    public Explode(int x, int y,TankClient tc) {
        this.x = x;
        this.y = y;
        this.tc=tc;
    }

    public void draw(Graphics g){
        if(!init){
            for(int i=0;i<imgs.length;i++){
                g.drawImage(imgs[i],-100,-100,null);
            }
            init=true;
        }

        if(!live) {
            tc.explodes.remove(this);
            return;
        }

        if(step==imgs.length){
            live=false;
            step=0;
            return;
        }
        /*
        Color c=g.getColor();
        g.setColor(Color.ORANGE);
        g.fillOval(x,y,diameter[step],diameter[step]);
        g.setColor(c);
        直接画图片即可
         */
        g.drawImage(imgs[step],x,y,null);

        step++;
    }

}

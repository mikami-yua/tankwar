import java.io.IOException;
import java.util.Properties;

public class PropertyMgr {
    static Properties props=new Properties();
    static {//写入static中避免每次调用方法都磁盘load一次
        try {
            props.load(PropertyMgr.class.getClassLoader().getResourceAsStream("config/tank.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getProperties(String key){
//        try {
//            props.load(PropertyMgr.class.getClassLoader().getResourceAsStream("config/tank.properties"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return props.getProperty(key);
    }
}

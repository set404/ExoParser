package config;

import java.io.FileInputStream;
import java.io.IOException;

public class Property {
    public static String exoUserName;
    public static String exoPassword;
    public static String tfEmail;
    public static String tfPassword;
    public static String downloadDir;
    public static String adEmail;
    public static String adPassword;


    static {
        java.util.Properties property = new java.util.Properties();
        FileInputStream fis;
        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exoUserName = property.getProperty("exo.username");
        exoPassword = property.getProperty("exo.password");
        tfEmail = property.getProperty("tf.email");
        tfPassword = property.getProperty("tf.password");
        downloadDir = property.getProperty("download.dir");
        adEmail = property.getProperty("ad.email");
        adPassword = property.getProperty("ad.password");

    }
}

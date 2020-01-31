package tests.Utilities;


import java.io.FileInputStream;
import java.util.Properties;

public class configReader {
    private static Properties configFile;


    static {

        try {
            String path = "src/test/resources/PropertiesFiles/configuration.properties";
            FileInputStream input = new FileInputStream(path);

            configFile = new Properties();
            configFile.load(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String keyName) {
        return configFile.getProperty(keyName);

    }

}

package sample;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by Santer on 20.02.2016.
 */
public class LoadDataFromPropToView {

    public static Map<String, String> getDataFromProp(String path) throws IOException {
        Map<String, String> map = new TreeMap<>();
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(path);

        //
        properties.load(inputStream);
        inputStream.close();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey().toString().startsWith("Exercise")) {
                map.put(entry.getKey().toString(), new String(entry.getValue().toString().getBytes("ISO8859-1")));
            }
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
    }
}

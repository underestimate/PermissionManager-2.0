package io.github.djxy.permissionManager.util;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-07.
 */
public class ResourceUtil {

    private static final ConcurrentHashMap<String,JsonArray> jsonArrays = new ConcurrentHashMap<>();

    public synchronized static JsonArray loadJsonArray(String fileName){
        Preconditions.checkNotNull(fileName, "fileName");

        if(jsonArrays.containsKey(fileName))
            return jsonArrays.get(fileName);

        File file = new File(ClassLoader.getSystemClassLoader().getResource(fileName).getFile());

        StringBuilder builder = new StringBuilder((int) file.length());

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;

        try {
            while((line = br.readLine()) != null)
                builder.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        jsonArrays.put(fileName, new JsonParser().parse(builder.toString()).getAsJsonArray());

        return jsonArrays.get(fileName);
    }

}

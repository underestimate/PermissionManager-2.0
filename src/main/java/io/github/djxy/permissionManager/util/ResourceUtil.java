package io.github.djxy.permissionmanager.util;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.github.djxy.permissionmanager.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-07.
 */
public class ResourceUtil {

    private final static Logger LOGGER = new Logger(ResourceUtil.class);

    private static final ConcurrentHashMap<String,JsonArray> jsonArrays = new ConcurrentHashMap<>();
    private static final Object object = new ResourceUtil();

    public synchronized static JsonArray loadJsonArray(String fileName) {
        Preconditions.checkNotNull(fileName, "fileName");

        if(jsonArrays.containsKey(fileName))
            return jsonArrays.get(fileName);

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(object.getClass().getClassLoader().getResource(fileName).openStream()));
            String line;

            StringBuilder builder = new StringBuilder(1024);

            while((line = br.readLine()) != null)
                builder.append(line);

            br.close();

            jsonArrays.put(fileName, new JsonParser().parse(builder.toString()).getAsJsonArray());

            LOGGER.info(fileName + " loaded.");

            return jsonArrays.get(fileName);
        } catch(Exception e){
            LOGGER.error("Couldn't read "+fileName+".");
            e.printStackTrace();
        }

        return new JsonArray();
    }

}

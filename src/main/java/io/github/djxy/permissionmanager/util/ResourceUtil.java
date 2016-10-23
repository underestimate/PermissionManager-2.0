package io.github.djxy.permissionmanager.util;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.github.djxy.permissionmanager.language.Language;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.translator.Translator;

import java.io.InputStream;
import java.nio.charset.Charset;
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
            InputStream is = object.getClass().getClassLoader().getResource(fileName).openStream();
            byte array[] = new byte[is.available()];
            is.read(array);
            is.close();

            jsonArrays.put(fileName, new JsonParser().parse(new String(array, Charset.forName("UTF-8"))).getAsJsonArray());

            LOGGER.info("Resource: "+fileName + " - Loaded.");

            return jsonArrays.get(fileName);
        } catch(Exception e){
            LOGGER.info("Resource: "+fileName + " - Can't be load.");
            e.printStackTrace();
        }

        return new JsonArray();
    }

    public synchronized static Translator loadTranslations() {
        Translator translator = new Translator();

        for (Language language : Language.getLanguages()) {
            try {
                InputStream is = object.getClass().getClassLoader().getResource("translations/"+language.getISO639_3()+".hocon").openStream();
                byte array[] = new byte[is.available()];
                is.read(array);
                is.close();

                String file = new String(array, Charset.forName("UTF-8"));
                String[] lines = file.split("\\n");

                for (String line : lines){
                    String key = line.substring(0, line.indexOf("\"")-1);
                    String value = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));

                    translator.addTranslation(language, key, value);
                }

                LOGGER.info("Translation: "+language.getISO639_3()+" - Loaded.");
            } catch (Exception e) {
                LOGGER.error("Translation: " + language.getISO639_3()+" - Can't be read.");
                e.printStackTrace();
            }
        }

        return translator;
    }

}

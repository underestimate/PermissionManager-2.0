package io.github.djxy.permissionmanager.language;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.djxy.permissionmanager.util.ResourceUtil;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-06.
 */
public class Language {

    private static boolean fileRead = false;
    private static final ArrayList<Language> languages = new ArrayList<>();
    private static final ConcurrentHashMap<String,Language> languageByISO639_3 = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,Language> languageByName = new ConcurrentHashMap<>();
    private static Language defaultLanguage;

    static {
        load();
    }

    public static Language getDefault(){
        return defaultLanguage;
    }

    public static Language getLanguage(String language){
        Preconditions.checkNotNull(language, "language");

        if(languageByISO639_3.containsKey(language))
            return languageByISO639_3.get(language);
        if(languageByName.containsKey(language))
            return languageByName.get(language);
        else
            return null;
    }

    public static ArrayList<Language> getLanguages() {
        return new ArrayList<>(languages);
    }

    public synchronized static void load(){
        if(fileRead)
            return;

        JsonArray jsonArray = ResourceUtil.loadJsonArray("languages.json");
        fileRead = true;

        for(int i = 0; i < jsonArray.size(); i++){
            Language language = new Language(jsonArray.get(i).getAsJsonObject());

            languages.add(language);
            languageByISO639_3.put(language.getISO639_3(), language);
            languageByName.put(language.getName(), language);

            if(language.getISO639_3().equals("eng"))
                defaultLanguage = language;
        }
    }

    private final JsonObject jsonObject;

    private Language(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public String getISO639_3(){
        return jsonObject.get("iso639-3").getAsString();
    }

    public String getName(){
        return jsonObject.get("name").getAsString();
    }

}

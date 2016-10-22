package io.github.djxy.permissionmanager.translator;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionmanager.language.Language;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-07.
 */
public class Translator {

    private final ConcurrentHashMap<Language, ConcurrentHashMap<String, String>> translations = new ConcurrentHashMap<>();

    public Translator() {
    }

    public void addTranslation(Language language, String code, String translation) {
        Preconditions.checkNotNull(language);
        Preconditions.checkNotNull(code);
        Preconditions.checkNotNull(translation);

        if(!translations.containsKey(language))
            translations.put(language, new ConcurrentHashMap<>());

        translations.get(language).put(code, translation);
    }

    public String getTranslation(Language language, String code) {
        Preconditions.checkNotNull(language);
        Preconditions.checkNotNull(code);

        if(translations.containsKey(language) && translations.get(language).containsKey(code))
            return translations.get(language).get(code);

        return translations.get(Language.getDefault()).get(code);
    }

}

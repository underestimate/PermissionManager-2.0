package io.github.djxy.permissionmanager.commands.parsers;

import io.github.djxy.customcommands.parsers.Parser;
import io.github.djxy.permissionmanager.language.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-23.
 */
public class LanguageParser extends Parser<Language> {

    private static List<String> suggestions = new ArrayList<>();

    @Override
    public Language parse(String value) {
        return Language.getLanguage(value);
    }

    @Override
    public List<String> getSuggestions(String value) {
        return getSuggestions(suggestions, value);
    }

    private synchronized void initSuggestions(){
        if(!suggestions.isEmpty())
            return;

        for(Language language : Language.getLanguages()) {
            suggestions.add(language.getName());
            suggestions.add(language.getISO639_3());
        }
    }

}

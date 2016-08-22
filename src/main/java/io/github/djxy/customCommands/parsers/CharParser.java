package io.github.djxy.customcommands.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-01.
 */
public class CharParser extends Parser<Character> {
    
    @Override
    public Character parse(String value) {
        return value.length() == 1?value.charAt(0):null;
    }

    @Override
    public List<String> getSuggestions(String value) {
        return new ArrayList<>();
    }

}

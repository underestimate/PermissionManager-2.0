package io.github.djxy.customcommands.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-01.
 */
public class ShortParser extends Parser<Short> {
    
    @Override
    public Short parse(String value) {
        try{
            return Short.parseShort(value);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String value) {
        return new ArrayList<>();
    }

}

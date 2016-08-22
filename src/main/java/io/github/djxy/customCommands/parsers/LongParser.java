package io.github.djxy.customcommands.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-01.
 */
public class LongParser extends Parser<Long> {
    
    @Override
    public Long parse(String value) {
        try{
            return Long.parseLong(value);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String value) {
        return new ArrayList<>();
    }

}

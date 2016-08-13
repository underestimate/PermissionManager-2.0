package io.github.djxy.customCommands.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-01.
 */
public class FloatParser extends Parser<Float> {
    
    @Override
    public Float parse(String value) {
        try{
            return Float.parseFloat(value);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String value) {
        return new ArrayList<>();
    }

}

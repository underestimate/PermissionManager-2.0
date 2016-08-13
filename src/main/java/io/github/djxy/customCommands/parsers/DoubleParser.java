package io.github.djxy.customCommands.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-01.
 */
public class DoubleParser extends Parser<Double> {
    
    @Override
    public Double parse(String value) {
        try{
            return Double.parseDouble(value);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String value) {
        return new ArrayList<>();
    }

}

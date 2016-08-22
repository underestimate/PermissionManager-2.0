package io.github.djxy.customcommands.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-01.
 */
public class ByteParser extends Parser<Byte> {
    
    @Override
    public Byte parse(String value) {
        try{
            return Byte.parseByte(value);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String value) {
        return new ArrayList<>();
    }

}

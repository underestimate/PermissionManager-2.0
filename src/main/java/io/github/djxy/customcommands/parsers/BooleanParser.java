package io.github.djxy.customcommands.parsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Samuel on 2016-07-28.
 */
public class BooleanParser extends Parser<Boolean> {

    private static final Map<String,Boolean> values = new HashMap<>();

    static {
        values.put("true", true);
        values.put("false", false);
    }

    public BooleanParser() {
    }

    @Override
    public Boolean parse(String value) {
        return values.get(value.toLowerCase());
    }

    @Override
    public List<String> getSuggestions(String value) {
        return getSuggestions(values.keySet(), value);
    }

}

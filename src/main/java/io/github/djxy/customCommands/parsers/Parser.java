package io.github.djxy.customcommands.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Samuel on 2016-07-28.
 */
public abstract class Parser<V> {

    abstract public V parse(String value);

    abstract public List<String> getSuggestions(String value);

    protected List<String> getSuggestions(Collection<String> values, String value){
        ArrayList<String> suggestions = new ArrayList<>();
        value = value.toLowerCase();

        for(String val : values)
            if(val.toLowerCase().startsWith(value) && !val.equalsIgnoreCase(value))
                suggestions.add(val);

        return suggestions;
    }
}

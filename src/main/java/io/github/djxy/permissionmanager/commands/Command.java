package io.github.djxy.permissionmanager.commands;

import com.google.common.collect.ImmutableMap;
import io.github.djxy.permissionmanager.language.Language;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.TranslationParser;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel on 2016-08-22.
 */
public class Command {

    protected static final Map EMPTY_MAP = ImmutableMap.copyOf(new HashMap<>());

    protected final TranslationParser parser = new TranslationParser(TextSerializers.FORMATTING_CODE.deserialize("&f[&6Permission&l&4M&r&f] "));
    protected final Translator translator;

    public Command(Translator translator) {
        this.translator = translator;
    }

    public Language getLanguage(CommandSource source){
        if(source instanceof Player)
            return ((User) UserCollection.instance.get(((Player) source).getUniqueId().toString())).getLanguage();
        else
            return Language.getDefault();
    }

    public Map<String,String> createVariableMap(String key, String value){
        HashMap<String,String> variables = new HashMap<>();

        variables.put(key, value);

        return variables;
    }

    public Map<String,TextAction> createVariableMap(String key, TextAction value){
        HashMap<String,TextAction> variables = new HashMap<>();

        variables.put(key, value);

        return variables;
    }

    public Map<String,String> createVariableMap(String key1, String value1, String key2, String value2){
        HashMap<String,String> variables = new HashMap<>();

        variables.put(key1, value1);
        variables.put(key2, value2);

        return variables;
    }

    public Map<String,String> createVariableMap(String key1, String value1, String key2, String value2, String key3, String value3){
        HashMap<String,String> variables = new HashMap<>();

        variables.put(key1, value1);
        variables.put(key2, value2);
        variables.put(key3, value3);

        return variables;
    }

    public Map<String,String> createVariableMap(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4){
        HashMap<String,String> variables = new HashMap<>();

        variables.put(key1, value1);
        variables.put(key2, value2);
        variables.put(key3, value3);
        variables.put(key4, value4);

        return variables;
    }

}

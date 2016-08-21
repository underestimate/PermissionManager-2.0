package io.github.djxy.permissionManager.commands;

import io.github.djxy.customCommands.annotations.CustomCommand;
import io.github.djxy.customCommands.annotations.CustomParser;
import io.github.djxy.customCommands.parsers.BooleanParser;
import io.github.djxy.permissionManager.translator.TranslationParser;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel on 2016-08-13.
 */
public class CreateGroupCommand {

    @CustomCommand(
            command = "pm #player #visible #argument...",
            permission = "bob.rtsad.123",
            parsers = {
                    @CustomParser(argument = "player", parser = BooleanParser.class),
                    @CustomParser(argument = "visible", parser = BooleanParser.class)
            }
    )
    public void test(CommandSource source, Map<String,Object> values) {
        TranslationParser parser = new TranslationParser();

        HashMap<String,String> map = new HashMap<>();

        map.put("name", source.getName());

        HashMap<String,String> mapSub = new HashMap<>();

        mapSub.put("clickHere", "cliquez ici");

        HashMap<String,TextAction> mapAction = new HashMap<>();

        mapAction.put("clickHere", TextActions.executeCallback(source1 -> {System.out.println("Bob");}));

        source.sendMessage(parser.parse("Salut {name}, sa va bien? {clickHere}", mapSub, map, mapAction));
        System.out.println(source.getName());
        System.out.println(values);
    }

}

package io.github.djxy.permissionManager.commands;

import io.github.djxy.customCommands.annotations.CustomCommand;
import io.github.djxy.customCommands.annotations.CustomParser;
import io.github.djxy.customCommands.parsers.BooleanParser;

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
    public void test(Map<String,Object> values){

    }

}

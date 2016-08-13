package io.github.djxy.customCommands;

import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-06-01.
 */
public interface CommandExecutor {

    public void execute(CommandSource source, Map<String, Object> values);

}

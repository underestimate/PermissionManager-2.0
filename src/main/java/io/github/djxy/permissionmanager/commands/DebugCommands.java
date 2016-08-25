package io.github.djxy.permissionmanager.commands;

import io.github.djxy.customcommands.annotations.CustomCommand;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.logger.LoggerMode;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.command.CommandSource;

import java.util.Map;

/**
 * Created by Samuel on 2016-08-13.
 */
public class DebugCommands extends Command{

    private final static String PERMISSION_MODE_SET = "permissionmanager.commands.debug.set";
    private final static String PERMISSION_MODE_GET = "permissionmanager.commands.debug.get";

    public DebugCommands(Translator translator) {
        super(translator);
    }

    @CustomCommand(
            command = "pm debug on",
            permission = PERMISSION_MODE_SET,
            parsers = {}
    )
    public void setDebugOn(CommandSource source, Map<String, Object> values) {
        source.sendMessage(parser.parse(translator.getTranslation(getLanguage(source), "debug_set_state"), EMPTY_MAP, createVariableMap("state", translator.getTranslation(getLanguage(source), "debug_state_on")), EMPTY_MAP));
        Logger.setLoggerMode(LoggerMode.DEBUG_SERVER);
    }

    @CustomCommand(
            command = "pm debug off",
            permission = PERMISSION_MODE_SET,
            parsers = {}
    )
    public void setDebugOff(CommandSource source, Map<String, Object> values) {
        source.sendMessage(parser.parse(translator.getTranslation(getLanguage(source), "debug_set_state"), EMPTY_MAP, createVariableMap("state", translator.getTranslation(getLanguage(source), "debug_state_off")), EMPTY_MAP));
        Logger.setLoggerMode(LoggerMode.NO_LOG);
    }

    @CustomCommand(
            command = "pm debug",
            permission = PERMISSION_MODE_GET,
            parsers = {}
    )
    public void getDebugState(CommandSource source, Map<String, Object> values) {
        source.sendMessage(parser.parse(translator.getTranslation(getLanguage(source), "debug_get_state"), EMPTY_MAP, createVariableMap("state", Logger.getLoggerMode() == LoggerMode.NO_LOG ? translator.getTranslation(getLanguage(source), "debug_state_off") : translator.getTranslation(getLanguage(source), "debug_state_on")), EMPTY_MAP));
    }

}

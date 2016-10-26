package io.github.djxy.permissionmanager.commands;

import io.github.djxy.customcommands.annotations.CustomCommand;
import io.github.djxy.customcommands.annotations.CustomParser;
import io.github.djxy.permissionmanager.commands.parsers.GroupParser;
import io.github.djxy.permissionmanager.commands.parsers.LanguageParser;
import io.github.djxy.permissionmanager.commands.parsers.UserParser;
import io.github.djxy.permissionmanager.language.Language;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Samuel on 2016-08-23.
 */
public class UserCommands extends SubjectCommands {

    private static final String PERMISSION_LANGUAGE_SET = "permissionmanager.commands.users.set.language";

    public UserCommands(Translator translator) {
        super(translator, UserCollection.instance, "users", "user", new UserParser());
    }

    @CustomCommand(
            command = "pm load users",
            permission = "permissionmanager.commands.users.load",
            parsers = {}
    )
    public void loadOnlineUsers(CommandSource source, Map<String, Object> values){
        UserCollection.instance.load();

        source.sendMessage(
                parser.parse(
                        translator.getTranslation(getLanguage(source), "user_loaded_all"),
                        EMPTY_MAP,
                        EMPTY_MAP,
                        EMPTY_MAP
                )
        );
    }

    @CustomCommand(
            command = "pm users #user language #language",
            permission = PERMISSION_LANGUAGE_SET,
            parsers = {
                    @CustomParser(argument = "language", parser = LanguageParser.class),
                    @CustomParser(argument = "user", parser = UserParser.class)
            }
    )
    public void setUserLanguage(CommandSource source, Map<String, Object> values) {
        User user = (User) values.get("user");
        Language language = (Language) values.get("language");

        user.setLanguage(language);
        source.sendMessage(
                parser.parse(translator.getTranslation(getLanguage(source), "language_user_set"),
                EMPTY_MAP,
                createVariableMap(
                        "user", Sponge.getServer().getGameProfileManager().getCache().getById(user.getUniqueId()).get().getName().get(),
                        "language", language.getName()
                ),
                EMPTY_MAP)
        );
    }

    @CustomCommand(
            command = "pm language #language",
            parsers = {@CustomParser(argument = "language", parser = LanguageParser.class)}
    )
    public void setLanguage(CommandSource source, Map<String, Object> values) {
        Language language = (Language) values.get("language");

        if(source instanceof Player)
            ((User) UserCollection.instance.get(((Player) source).getUniqueId().toString())).setLanguage(language);

        source.sendMessage(
                parser.parse(translator.getTranslation(getLanguage(source), "language_set"),
                EMPTY_MAP,
                createVariableMap("language", getLanguage(source).getName()),
                EMPTY_MAP)
        );
    }

    @CustomCommand(
            command = "pm language",
            parsers = {}
    )
    public void getLanguage(CommandSource source, Map<String, Object> values) {
        source.sendMessage(
                parser.parse(translator.getTranslation(getLanguage(source), "language_get"),
                EMPTY_MAP,
                createVariableMap("language", getLanguage(source).getName()),
                EMPTY_MAP)
        );
    }

    @Override
    String getSubjectName(Subject subject) {
        Optional<String> name = Sponge.getServer().getGameProfileManager().getCache().getById(((User) subject).getUniqueId()).get().getName();
        return name.isPresent()?name.get():"";
    }
}

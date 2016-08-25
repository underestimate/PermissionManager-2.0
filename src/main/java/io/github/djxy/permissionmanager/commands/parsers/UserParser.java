package io.github.djxy.permissionmanager.commands.parsers;

import io.github.djxy.customcommands.parsers.Parser;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Samuel on 2016-08-23.
 */
public class UserParser extends Parser<User> {

    @Override
    public User parse(String value) {
        Optional<GameProfile> profil = Sponge.getServer().getGameProfileManager().getCache().getByName(value);

        return profil.isPresent() ? (User) UserCollection.instance.get(profil.get().getUniqueId().toString()) : null;
    }

    @Override
    public List<String> getSuggestions(String value) {
        List<String> names = new ArrayList<>();
        Collection<GameProfile> gameProfiles = Sponge.getServer().getGameProfileManager().getCache().match(value);

        if(gameProfiles.size() == 0)
            return names;

        if(gameProfiles.size() == 1) {
            GameProfile firstGameProfile = gameProfiles.iterator().next();

            if(firstGameProfile.getName().isPresent() && firstGameProfile.getName().get().equalsIgnoreCase(value))
                return names;
        }

        for(GameProfile gameProfile : gameProfiles)
            if(gameProfile.getName().isPresent())
                names.add(gameProfile.getName().get());

        return names;
    }

}

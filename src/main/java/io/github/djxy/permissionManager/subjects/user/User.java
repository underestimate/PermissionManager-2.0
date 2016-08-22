package io.github.djxy.permissionmanager.subjects.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.language.Language;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.rules.Rule;
import io.github.djxy.permissionmanager.subjects.ContextContainer;
import io.github.djxy.permissionmanager.subjects.Permission;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-09.
 */
public class User extends Subject {

    private final static Logger LOGGER = new Logger(User.class);

    private final UUID uuid;
    private Language language = Language.getDefault();
    private final CopyOnWriteArrayList<String> commandsOnCurrentTick = new CopyOnWriteArrayList<>();
    private long tickOfLastCommands = -1;

    protected User(UUID uuid, UserCollection userCollection) {
        super(uuid.toString(), userCollection);
        this.uuid = uuid;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void addCommandCurrentTick(String command){
        Preconditions.checkNotNull(command);
        long tick = Sponge.getServer().getRunningTimeTicks();

        LOGGER.info(command);

        if(tickOfLastCommands < tick)
            commandsOnCurrentTick.clear();

        tickOfLastCommands = tick;
        commandsOnCurrentTick.add(command);
    }

    public List<String> getCommandsOnCurrentTick(){
        if(Sponge.getServer().getRunningTimeTicks() == tickOfLastCommands)
            return (List<String>) commandsOnCurrentTick.clone();

        return new ArrayList<>();
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return getPlayer().isPresent()?Optional.of(getPlayer().get()):Optional.empty();
    }

    @Override
    public Optional<String> getOption(Set<Context> set, String key) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(key);
        Context context = null;

        if(ContextUtil.isGlobalContext(set)) {
            if(!getPlayerWorld().isPresent())
                return Optional.empty();

            context = new Context(Context.WORLD_KEY, getPlayerWorld().get());
        }
        if(ContextUtil.isSingleContext(set))
            context = ContextUtil.getContext(set);

        if(context == null)
            return Optional.empty();

        ContextContainer container = contexts.get(context);

        if(container != null) {
            String value = container.getOption(key);

            if (value != null)
                return Optional.of(value);
        }

        String value = globalContext.getOption(key);

        if(value != null)
            return Optional.of(value);

        set = Sets.newHashSet(context);

        if(container != null) {
            for (Group group : container.getGroups()) {
                Optional<String> valueOpt = group.getOption(set, key);

                if (valueOpt.isPresent())
                    return valueOpt;
            }
        }

        for (Group group : globalContext.getGroups()) {
            Optional<String> valueOpt = group.getOption(set, key);

            if (valueOpt.isPresent())
                return valueOpt;
        }

        return Optional.empty();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String permission) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);

        LOGGER.info(getIdentifier()+" get permission value for "+permission);

        Context context = null;

        if(ContextUtil.isGlobalContext(set)) {
            if(!getPlayerWorld().isPresent())
                return Tristate.UNDEFINED;

            context = new Context(Context.WORLD_KEY, getPlayerWorld().get());
        }
        if(ContextUtil.isSingleContext(set))
            context = ContextUtil.getContext(set);

        if(context == null)
            return Tristate.UNDEFINED;

        ContextContainer container = contexts.get(context);

        if(container != null) {
            Permission perm = container.getPermissions().getPermission(permission);

            if (perm != null)
                return Tristate.fromBoolean(perm.getValue());
        }

        Permission perm = globalContext.getPermissions().getPermission(permission);

        if(perm != null)
            return testPermissionRules(perm);

        set = Sets.newHashSet(context);

        if(container != null) {
            for (Group group : container.getGroups()) {
                perm = group.getPermissionValue(set, permission, new ArrayList<>());

                if (perm != null)
                    return testPermissionRules(perm);
            }
        }

        for (Group group : globalContext.getGroups()) {
            perm = group.getPermissionValue(set, permission, new ArrayList<>());

            if (perm != null)
                return testPermissionRules(perm);
        }

        return Tristate.UNDEFINED;
    }

    /**
     *
     * @param permission
     * @return Tristate.FALSE = can,t apply rules. Tristate.TRUE = can apply rules
     */
    private Tristate testPermissionRules(Permission permission){
        List<Rule> rules = permission.getRules();

        if(!getPlayer().isPresent())
            return Tristate.UNDEFINED;

        if(rules.isEmpty())
            return Tristate.fromBoolean(permission.getValue());

        Player player = getPlayer().get();

        for(Rule rule : rules)
            if(!rule.canApply(player))
                return Tristate.FALSE;

        for(Rule rule : rules)
            rule.apply(player);

        return Tristate.TRUE;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);

        language = node.getNode("language").getString("").isEmpty()?Language.getDefault():Language.getLanguage(node.getNode("language").getString(""));
    }

    @Override
    public void serialize(ConfigurationNode node) {
        super.serialize(node);

        node.getNode("language").setValue(language == null ? null : language.getName());
    }

    private Optional<Player> getPlayer(){
        return Sponge.getServer().getPlayer(uuid);
    }

    private Optional<String> getPlayerWorld(){
        return getPlayer().isPresent()?Optional.of(getPlayer().get().getWorld().getName()):Optional.empty();
    }

}

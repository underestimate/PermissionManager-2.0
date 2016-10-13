package io.github.djxy.permissionmanager.subjects.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.PermissionService;
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

    public UUID getUniqueId() {
        return uuid;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        Preconditions.checkNotNull(language);

        this.language = language;
    }

    public void addCommandCurrentTick(String command){
        Preconditions.checkNotNull(command);
        long tick = Sponge.getServer().getRunningTimeTicks();

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

        if(ContextUtil.isGlobalContext(set)) {
            if(getPlayerWorld().isPresent()) {
                Set<Context> worldContext = Sets.newHashSet(new Context(Context.WORLD_KEY, getPlayerWorld().get()));

                if(contexts.containsKey(worldContext)) {
                    String value = contexts.get(worldContext).getOption(key);

                    if (value != null)
                        return Optional.of(value);
                }
            }
        }
        else if(contexts.containsKey(set)){
            String value = contexts.get(set).getOption(key);

            if (value != null)
                return Optional.of(value);
        }

        if(contexts.containsKey(GLOBAL_CONTEXT)){
            ContextContainer globalContainer = contexts.get(GLOBAL_CONTEXT);

            String value = globalContainer.getOption(key);

            if (value != null)
                return Optional.of(value);
        }

        if(ContextUtil.isGlobalContext(set)) {
            if(getPlayerWorld().isPresent()) {
                Set<Context> worldContext = Sets.newHashSet(new Context(Context.WORLD_KEY, getPlayerWorld().get()));

                if(contexts.containsKey(worldContext)) {
                    for (Group group : contexts.get(worldContext).getGroups()) {
                        Optional<String> valueOpt = group.getOption(worldContext, key);

                        if (valueOpt.isPresent())
                            return valueOpt;
                    }
                }

                if(contexts.containsKey(GLOBAL_CONTEXT)){
                    for (Group group : contexts.get(GLOBAL_CONTEXT).getGroups()) {
                        Optional<String> valueOpt = group.getOption(worldContext, key);

                        if (valueOpt.isPresent())
                            return valueOpt;
                    }
                }
            }
        }
        else if(contexts.containsKey(set)){
            for (Group group : contexts.get(set).getGroups()) {
                Optional<String> valueOpt = group.getOption(set, key);

                if (valueOpt.isPresent())
                    return valueOpt;
            }
        }

        if(contexts.containsKey(GLOBAL_CONTEXT)){
            for (Group group : contexts.get(GLOBAL_CONTEXT).getGroups()) {
                Optional<String> valueOpt = group.getOption(set, key);

                if (valueOpt.isPresent())
                    return valueOpt;
            }
        }

        return Optional.empty();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String permission) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);

        LOGGER.info(getIdentifier() + " get permission value for " + permission+" - "+set);

        if(ContextUtil.isGlobalContext(set)) {
            if(getPlayerWorld().isPresent()) {
                Set<Context> worldContext = Sets.newHashSet(new Context(Context.WORLD_KEY, getPlayerWorld().get()));

                if(contexts.containsKey(worldContext)){
                    Permission value = contexts.get(worldContext).getPermissions().getPermission(permission);

                    if (value != null)
                        return testPermissionRules(value);
                }
            }
        }
        else if(contexts.containsKey(set)){
            Permission value = contexts.get(set).getPermissions().getPermission(permission);

            if (value != null)
                return testPermissionRules(value);
        }

        if(contexts.containsKey(GLOBAL_CONTEXT)){
            Permission value = contexts.get(GLOBAL_CONTEXT).getPermissions().getPermission(permission);

            if (value != null)
                return testPermissionRules(value);
        }

        if(ContextUtil.isGlobalContext(set)) {
            if(getPlayerWorld().isPresent()) {
                Set<Context> worldContext = Sets.newHashSet(new Context(Context.WORLD_KEY, getPlayerWorld().get()));

                if(contexts.containsKey(worldContext)) {
                    for (Group group : contexts.get(worldContext).getGroups()) {
                        Permission perm = group.getPermissionValue(worldContext, permission, new ArrayList<>());

                        if (perm != null)
                            return testPermissionRules(perm);
                    }
                }

                if(contexts.containsKey(GLOBAL_CONTEXT)) {
                    for (Group group : contexts.get(GLOBAL_CONTEXT).getGroups()) {
                        Permission perm = group.getPermissionValue(worldContext, permission, new ArrayList<>());

                        if (perm != null)
                            return testPermissionRules(perm);
                    }
                }
            }
        }
        else if(contexts.containsKey(set)){
            for (Group group : contexts.get(set).getGroups()) {
                Permission perm = group.getPermissionValue(set, permission, new ArrayList<>());

                if (perm != null)
                    return testPermissionRules(perm);
            }
        }

        if(contexts.containsKey(GLOBAL_CONTEXT)) {
            for (Group group : contexts.get(GLOBAL_CONTEXT).getGroups()) {
                Permission perm = group.getPermissionValue(set, permission, new ArrayList<>());

                if (perm != null)
                    return testPermissionRules(perm);
            }
        }

        return PermissionService.instance.getDefaults().getPermissionValue(set, permission);
    }

    /**
     *
     * @param permission
     * @return Tristate.FALSE = can,t apply rules. Tristate.TRUE = can apply rules
     */
    private Tristate testPermissionRules(Permission permission){
        List<Rule> rules = permission.getRules();

        if(rules.isEmpty())
            return Tristate.fromBoolean(permission.getValue());

        if(!getPlayer().isPresent())
            return Tristate.UNDEFINED;

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

        language = node.getNode("options", "language").getString("").isEmpty()?Language.getDefault():Language.getLanguage(node.getNode("options", "language").getString(""));

        language = language == null?Language.getDefault():language;
    }

    @Override
    public void serialize(ConfigurationNode node) {
        super.serialize(node);

        node.getNode("options", "language").setValue(language == null ? null : language.getISO639_3());
    }

    private Optional<Player> getPlayer(){
        return isSpongeInitialized()?Sponge.getServer().getPlayer(uuid):Optional.empty();
    }

    private Optional<String> getPlayerWorld(){
        return isSpongeInitialized() && getPlayer().isPresent()?Optional.of(getPlayer().get().getWorld().getName()):Optional.empty();
    }

    private boolean isSpongeInitialized(){
        try{
            Sponge.getGame();
            return true;
        } catch (Exception e){
            return false;
        }
    }

}

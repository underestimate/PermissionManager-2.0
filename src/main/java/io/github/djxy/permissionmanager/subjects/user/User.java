package io.github.djxy.permissionmanager.subjects.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionmanager.language.Language;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.rules.Rule;
import io.github.djxy.permissionmanager.rules.Rules;
import io.github.djxy.permissionmanager.subjects.Permission;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.special.Default;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-09.
 */
public class User extends Subject implements Locatable {

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
            return ImmutableList.copyOf(commandsOnCurrentTick);

        return new ArrayList<>();
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return getPlayer().isPresent()?Optional.of(getPlayer().get()):Optional.empty();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String permission) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);
        Permission perm = getPermission(this, set, permission);

        if(perm == null)
            perm = Default.instance.getPermission(this, set, permission);
        else
            return testPermissionRules(perm);

        return perm == null?Tristate.UNDEFINED:testPermissionRules(perm);
    }

    @Override
    public Optional<String> getOption(Set<Context> set, String s) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(s);
        String value = getOption(this, set, s);

        if(value == null)
            value = Default.instance.getOption(this, set, s);
        else
            return Optional.of(value);

        return value == null?Optional.empty():Optional.of(value);
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

        for(Rule rule : rules) {
            if (!rule.canApply(player)) {
                LOGGER.info("User: "+getIdentifier()+" - Can't apply rule "+ Rules.instance.getName(rule.getClass()));
                return Tristate.FALSE;
            }
        }

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

    @Override
    public Location<World> getLocation() {
        return getPlayer().isPresent()?getPlayer().get().getLocation():null;
    }

    @Override
    public World getWorld() {
        return getPlayer().isPresent()?getPlayer().get().getWorld():null;
    }

}

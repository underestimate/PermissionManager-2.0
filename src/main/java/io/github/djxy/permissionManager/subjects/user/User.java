package io.github.djxy.permissionManager.subjects.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionManager.area.Country;
import io.github.djxy.permissionManager.language.Language;
import io.github.djxy.permissionManager.rules.Rule;
import io.github.djxy.permissionManager.subjects.ContextContainer;
import io.github.djxy.permissionManager.subjects.Permission;
import io.github.djxy.permissionManager.subjects.Subject;
import io.github.djxy.permissionManager.subjects.group.Group;
import io.github.djxy.permissionManager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-08-09.
 */
public class User extends Subject {

    private final UUID uuid;
    private Language mainLanguage = Language.getDefault();
    private CopyOnWriteArraySet<Language> languages = new CopyOnWriteArraySet<>();
    private Country country;

    protected User(UUID uuid, UserCollection userCollection) {
        super(uuid.toString(), userCollection);
        this.uuid = uuid;
    }

    public Language getMainLanguage() {
        return mainLanguage;
    }

    public void setMainLanguage(Language mainLanguage) {
        this.mainLanguage = mainLanguage;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void addLanguage(Language language){
        languages.add(language);
    }

    public void removeLanguage(Language language){
        languages.remove(language);
    }

    public Set<Language> getLanguages() {
        return new CopyOnWriteArraySet<>(languages);
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.of(getPlayer());
    }

    @Override
    public Optional<String> getOption(Set<Context> set, String key) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(key);
        Context context = null;

        if(ContextUtil.isGlobalContext(set))
            context = new Context(Context.WORLD_KEY, getPlayerWorld());
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

        Context context = null;

        if(ContextUtil.isGlobalContext(set))
            context = new Context(Context.WORLD_KEY, getPlayerWorld());
        if(ContextUtil.isSingleContext(set))
            context = ContextUtil.getContext(set);

        if(context == null)
            return Tristate.UNDEFINED;

        ContextContainer container = contexts.get(context);

        if(container != null) {
            Permission perm = container.getPermissions().getPermission(permission);

            if (perm != null)
                return testPermissionRules(perm);
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

        if(rules.isEmpty())
            return Tristate.fromBoolean(permission.getValue());

        for(Rule rule : rules)
            if(!rule.canApply(getPlayer()))
                return Tristate.FALSE;

        for(Rule rule : rules)
            rule.apply(getPlayer());

        return Tristate.TRUE;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);

        country = Country.getCountry(node.getNode("country").getString(""));
        mainLanguage = node.getNode("languages", "main").getString("").isEmpty()?Language.getDefault():Language.getLanguage(node.getNode("languages", "main").getString(""));

        List<ConfigurationNode> permissionList = (List<ConfigurationNode>) node.getNode("languages", "others").getChildrenList();

        for(ConfigurationNode nodeValue : permissionList){
            String value = nodeValue.getString("");

            if(!value.isEmpty()){
                Language language = Language.getLanguage(value);

                if(language != null)
                    addLanguage(language);
            }
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        super.serialize(node);

        node.getNode("country").setValue(country == null ? null : country.getCommonName());
        node.getNode("languages", "main").setValue(mainLanguage == null?null:mainLanguage.getName());

        List<String> languages = new ArrayList<>();

        for(Language language : this.languages)
            languages.add(language.getName());

        node.getNode("languages", "others").setValue(languages);
    }

    private Player getPlayer(){
        return Sponge.getServer().getPlayer(uuid).get();
    }

    private String getPlayerWorld(){
        return "world";
    }

}

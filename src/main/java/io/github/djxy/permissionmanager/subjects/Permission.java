package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionmanager.rules.Rule;
import io.github.djxy.permissionmanager.rules.Rules;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-09.
 */
public class Permission implements ConfigurationNodeDeserializer, ConfigurationNodeSerializer {

    private final String permission;
    private boolean value;
    private final ConcurrentHashMap<Class<? extends Rule>, Rule> rules = new ConcurrentHashMap();

    public Permission(String permission, boolean value) {
        Preconditions.checkNotNull(permission);

        this.permission = permission;
        this.value = value;
    }

    public void addRule(Rule rule){
        rules.put(rule.getClass(), rule);
    }

    public List<Rule> getRules() {
        return ImmutableList.copyOf(rules.values());
    }

    public String getPermission() {
        return permission;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        Map<Object,ConfigurationNode> rulesMap = (Map<Object, ConfigurationNode>) node.getChildrenMap();

        for(Object ruleNode : rulesMap.keySet()){
            Rule rule = Rules.instance.getRule(ruleNode.toString());

            if(rule != null) {
                rule.deserialize(rulesMap.get(ruleNode));
                addRule(rule);
            }
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        for(Rule rule : rules.values()){
            String ruleName = Rules.instance.getName(rule.getClass());

            if(ruleName != null)
                rule.serialize(node.getNode(ruleName));
        }
    }

    @Override
    public String toString() {
        return "Permission{" +
                "permission='" + permission + '\'' +
                ", value=" + value +
                '}';
    }
}

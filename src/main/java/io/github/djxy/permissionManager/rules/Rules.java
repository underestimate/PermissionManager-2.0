package io.github.djxy.permissionManager.rules;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionManager.rules.home.HomeRule;
import io.github.djxy.permissionManager.rules.region.RegionRule;
import io.github.djxy.permissionManager.rules.time.TimeRule;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-16.
 */
public class Rules {

    public final static Rules instance = new Rules();

    private final ConcurrentHashMap<String, Class<? extends Rule>> rules = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends Rule>, String> ruleNames = new ConcurrentHashMap<>();

    private Rules() {
        //createRule("cooldown", CooldownRule.class);
        //createRule("economy", EconomyRule.class);
        createRule("home", HomeRule.class);
        createRule("region", RegionRule.class);
        createRule("time", TimeRule.class);
    }

    public void createRule(String name, Class<? extends Rule> rule){
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(rule);

        rules.put(name, rule);
        ruleNames.put(rule, name);
    }

    public String getName(Class<? extends Rule> clazz){
        Preconditions.checkNotNull(clazz);

        return ruleNames.get(clazz);
    }

    public Rule getRule(String name){
        Preconditions.checkNotNull(name);

        if(!rules.containsKey(name))
            return null;

        try {
            return rules.get(name).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

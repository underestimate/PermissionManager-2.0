package io.github.djxy.permissionmanager.util;

import org.spongepowered.api.service.context.Context;

import java.util.Set;

/**
 * Created by Samuel on 2016-08-09.
 */
public class ContextUtil {

    public static boolean isGlobalContext(Set<Context> set){
        return set.isEmpty();
    }

    public static boolean isSingleContext(Set<Context> set){
        return set.size() == 1;
    }

    public static Context getContext(Set<Context> set){
        return set.iterator().next();
    }

}

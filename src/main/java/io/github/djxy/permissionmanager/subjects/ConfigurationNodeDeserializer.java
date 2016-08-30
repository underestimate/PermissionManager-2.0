package io.github.djxy.permissionmanager.subjects;

import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Samuel on 2016-08-13.
 */
public interface ConfigurationNodeDeserializer {

    public void deserialize(ConfigurationNode node);

}

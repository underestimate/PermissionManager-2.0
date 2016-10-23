package io.github.djxy.permissionmanager.util;

import io.github.djxy.permissionmanager.logger.Logger;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Samuel on 2016-08-29.
 */
public class FileConversionUtil {

    private static final Logger LOGGER = new Logger(FileConversionUtil.class);

    public static void convertUsers(Path path){
        File file = path.resolve("players.yml").toFile();

        if(file.exists())
            convertSubjects(file, path.resolve("users"));
    }

    public static void convertGroups(Path path){
        File file = path.resolve("groups.yml").toFile();

        if(file.exists())
            convertSubjects(file, path.resolve("groups"));
    }

    private static void convertSubjects(File file, Path path){
        ConfigurationNode root;

        try {
            root = loadFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Map<Object,ConfigurationNode> subjects = (Map<Object, ConfigurationNode>) root.getChildrenMap();

        for(Map.Entry<Object,ConfigurationNode> pairs : subjects.entrySet()){
            File subjectFile = path.resolve(pairs.getKey().toString()+".yml").toFile();

            if(!subjectFile.exists()){
                try {
                    convertSubject(pairs.getValue());
                    saveNode(subjectFile, pairs.getValue());
                    LOGGER.info("Subject: "+pairs.getKey().toString()+" - Converted.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static void convertSubject(ConfigurationNode node){
        if(!node.getNode("rules").isVirtual())
            convertRules(node.getNode("rules"));

        if(!node.getNode("data").isVirtual())
            node.getNode("options").setValue(node.getNode("data").getChildrenMap());

        node.getNode("options", "prefix").setValue(node.getNode("prefix"));

        Map<Object,ConfigurationNode> worlds = (Map<Object, ConfigurationNode>) node.getNode("worlds").getChildrenMap();

        for(Map.Entry<Object,ConfigurationNode> pairs : worlds.entrySet()) {
            if(!node.getNode("rules").isVirtual())
                node.getNode("worlds", pairs.getKey(), "rules").setValue(node.getNode("rules").getChildrenMap());

            if (!node.getNode("worlds", pairs.getKey(), "data").isVirtual())
                node.getNode("worlds", pairs.getKey(), "options").setValue(node.getNode("worlds", pairs.getKey(), "data").getChildrenMap());
        }
    }

    private static void convertRules(ConfigurationNode node){
        Map<Object,ConfigurationNode> rulesNode = (Map<Object, ConfigurationNode>) node.getChildrenMap();

        for(Map.Entry<Object,ConfigurationNode> pairs : rulesNode.entrySet()){
            Map<Object,ConfigurationNode> rules = (Map<Object, ConfigurationNode>) pairs.getValue().getChildrenMap();

            for (Map.Entry<Object,ConfigurationNode> rule : rules.entrySet()) {
                String name = rule.getKey().toString();

                if(name.equals("cost"))
                    pairs.getValue().getNode("economy", "cost").setValue(rule.getValue().getNode("cost").getValue());
                if(name.equals("home"))
                    rule.getValue().setValue(true);
                if(name.equals("cooldown"))
                    rule.getValue().getNode("time").setValue(rule.getValue().getNode("cooldown").getValue());
                if(name.equals("region"))
                    rule.getValue().setValue(Arrays.asList(rule.getValue().getNode("location").getString()));
            }
        }
    }

    public static void convertPromotions(Path path){
        ConfigurationNode root;

        try {
            root = loadFile(path.resolve("promotions.yml").toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Map<Object,ConfigurationNode> subjects = (Map<Object, ConfigurationNode>) root.getChildrenMap();

        for(Map.Entry<Object,ConfigurationNode> pairs : subjects.entrySet()){
            File subjectFile = path.resolve("promotions").resolve(pairs.getKey().toString()+".yml").toFile();

            if(!subjectFile.exists()){
                try {
                    convertPromotion(pairs.getValue());
                    saveNode(subjectFile, pairs.getValue());
                    LOGGER.info("Promotion: "+pairs.getKey().toString()+" - Converted.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static void convertPromotion(ConfigurationNode node){
        if(!node.getNode("groups", "add").isVirtual())
            node.getNode("add", "groups").setValue(node.getNode("groups", "add").getChildrenList());

        if(!node.getNode("groups", "remove").isVirtual())
            node.getNode("remove", "groups").setValue(node.getNode("groups", "remove").getChildrenList());
    }

    public static void saveNode(File file, ConfigurationNode node) throws IOException {
        YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build().save(node);
    }

    public static ConfigurationNode loadFile(File file) throws IOException {
        return YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build().load();
    }
}

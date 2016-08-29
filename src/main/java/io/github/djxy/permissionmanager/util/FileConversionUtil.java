package io.github.djxy.permissionmanager.util;

import io.github.djxy.permissionmanager.logger.Logger;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
                    saveNode(subjectFile, pairs.getValue());
                    LOGGER.info(pairs.getKey().toString()+" converted.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void convertPromotions(Path path){

    }

    public static void saveNode(File file, ConfigurationNode node) throws IOException {
        YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build().save(node);
    }

    public static ConfigurationNode loadFile(File file) throws IOException {
        return YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build().load();
    }
}

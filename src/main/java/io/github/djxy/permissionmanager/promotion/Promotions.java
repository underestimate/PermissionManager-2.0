package io.github.djxy.permissionmanager.promotion;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionmanager.exceptions.PromotionNameExistException;
import io.github.djxy.permissionmanager.logger.Logger;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-17.
 */
public class Promotions {

    private static final Logger LOGGER = new Logger(Promotions.class);

    public static final Promotions instance = new Promotions();

    private final ConcurrentHashMap<String,Promotion> promotions = new ConcurrentHashMap<>();
    private Path directory;

    private Promotions(){
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        Preconditions.checkNotNull(directory);

        this.directory = directory;
    }

    public List<Promotion> getPromotions(){
        return ImmutableList.copyOf(promotions.values());
    }

    public synchronized void renamePromotion(Promotion promotion, String newName) throws PromotionNameExistException {
        Preconditions.checkNotNull(newName);
        Preconditions.checkNotNull(promotion);

        if(promotions.containsKey(newName))
            throw new PromotionNameExistException(newName+" already exist.");

        promotions.remove(promotion.getName());
        promotions.put(newName, promotion);

        LOGGER.info("Promotion: " + promotion.getName() + " - Renamed " + newName + ".");

        File file = directory.resolve(promotion.getName()+".yml").toFile();

        if(file.exists()) {
            file.delete();
            LOGGER.info(promotion.getName() + ".yml has been deleted.");
        }

        promotion.setName(newName);

        try {
            save(newName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Promotion createPromotion(String name) throws PromotionNameExistException {
        Preconditions.checkNotNull(name);

        if(promotions.containsKey(name))
            throw new PromotionNameExistException(name+" already exist.");

        Promotion promotion = new Promotion(name);

        promotions.put(name, promotion);

        LOGGER.info("Promotion: " + name + " - Created.");

        return promotion;
    }

    public Promotion getPromotion(String name){
        Preconditions.checkNotNull(name);

        return promotions.get(name);
    }

    public void deletePromotion(String name){
        Preconditions.checkNotNull(name);

        if(!promotions.containsKey(name))
            return;

        promotions.remove(name);
        LOGGER.info("Promotion: " + name + " - Deleted.");

        File file = directory.resolve(name+".yml").toFile();

        if(!file.exists())
            return;

        file.delete();
    }

    public void load(){
        File files[] = this.directory.toFile().listFiles();

        if(files == null)
            return;

        for(File file : files)
            if(file.getName().contains("."))
                load(file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    public synchronized  boolean load(String name){
        Preconditions.checkNotNull(name);

        directory.toFile().mkdirs();

        File file = directory.resolve(name+".yml").toFile();

        if(!file.exists())
            return false;

        Promotion promotion;

        LOGGER.info("Promotion: " + name + " - Loading started.");

        if(promotions.containsKey(name))
            promotion = promotions.get(name);
        else{
            try{
                promotion = createPromotion(name);
            }catch (Exception e){
                LOGGER.error("Promotion: " + name + " - Loading failed.");
                e.printStackTrace();
                return false;
            }
        }

        try{
            ConfigurationLoader loader = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build();
            ConfigurationNode node = loader.load();

            promotion.deserialize(node);
        } catch (Exception e) {
            LOGGER.error("Promotion: " + name + " - Loading failed.");
            e.printStackTrace();
            return false;
        }

        LOGGER.info("Promotion: " + name + " - Loaded.");
        return true;
    }

    public void save(){
        Enumeration<String> names = promotions.keys();

        while(names.hasMoreElements()) {
            try {
                save(names.nextElement());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean save(String name) throws IOException {
        Preconditions.checkNotNull(name);

        directory.toFile().mkdirs();

        File file = directory.resolve(name+".yml").toFile();

        if(!promotions.containsKey(name))
            return false;

        ConfigurationLoader loader = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build();
        ConfigurationNode node = loader.createEmptyNode();

        promotions.get(name).serialize(node);

        loader.save(node);

        LOGGER.info("Promotion: " + name + " - Saved.");
        return true;
    }

}

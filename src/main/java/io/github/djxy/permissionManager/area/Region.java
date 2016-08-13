package io.github.djxy.permissionManager.area;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import io.github.djxy.permissionManager.language.Language;
import io.github.djxy.permissionManager.util.ResourceUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Samuel on 2016-08-06.
 */
public class Region {

    private static boolean fileRead = false;
    private static final ArrayList<Region> regions = new ArrayList<>();
    private static final HashMap<String,Region> regionsByName = new HashMap<>();

    static {
        Language.load();
        load();
    }

    public static Region getRegion(String name){
        Preconditions.checkNotNull(name, "name");

        return regionsByName.get(name);
    }

    public static ArrayList<Region> getRegions() {
        return new ArrayList<>(regions);
    }

    public static void load(){
        if(fileRead)
            return;

        JsonArray jsonArray = ResourceUtil.loadJsonArray("countries.json");
        fileRead = true;

        for(int i = 0; i < jsonArray.size(); i++){
            String region = jsonArray.get(i).getAsJsonObject().get("region").getAsString();
            region = region.isEmpty()?"UNKNOWN":region;

            if(!regionsByName.containsKey(region)) {
                Region reg = new Region(region);

                regions.add(reg);
                regionsByName.put(region, reg);
            }
        }
    }

    private final String name;

    private Region(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

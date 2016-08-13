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
public class SubRegion {

    private static boolean fileRead = false;
    private static final ArrayList<SubRegion> subRegions = new ArrayList<>();
    private static final HashMap<String,SubRegion> subRegionsByName = new HashMap<>();
    private static final HashMap<Region,ArrayList<SubRegion>> subRegionsByRegion = new HashMap<>();

    static {
        Language.load();
        Region.load();
        load();
    }

    public static SubRegion getSubRegion(String name){
        Preconditions.checkNotNull(name, "name");

        return subRegionsByName.get(name);
    }

    public static ArrayList<SubRegion> getSubRegions() {
        return new ArrayList<>(subRegions);
    }

    public static ArrayList<SubRegion> getSubRegionByRegion(Region region){
        Preconditions.checkNotNull(region, "region");

        return new ArrayList<>(subRegionsByRegion.get(region));
    }

    public static void load(){
        if(fileRead)
            return;

        JsonArray jsonArray = ResourceUtil.loadJsonArray("countries.json");
        fileRead = true;

        for(int i = 0; i < jsonArray.size(); i++){
            String region = jsonArray.get(i).getAsJsonObject().get("region").getAsString();
            String subRegion = jsonArray.get(i).getAsJsonObject().get("subregion").getAsString();

            if(!subRegionsByRegion.containsKey(Region.getRegion(region)))
                subRegionsByRegion.put(Region.getRegion(region), new ArrayList<>());

            if(!subRegionsByName.containsKey(subRegion)) {
                SubRegion reg = new SubRegion(subRegion);

                subRegions.add(reg);
                subRegionsByName.put(subRegion, reg);
                subRegionsByRegion.get(Region.getRegion(region)).add(reg);
            }
        }
    }

    private final String name;

    private SubRegion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

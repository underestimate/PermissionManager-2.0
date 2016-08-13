package io.github.djxy.permissionManager.area;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.djxy.permissionManager.language.Language;
import io.github.djxy.permissionManager.util.ResourceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Samuel on 2016-08-06.
 */
public class Country {

    private static boolean fileRead = false;
    private static final ArrayList<Country> countries = new ArrayList<>();
    private static final HashMap<String, Country> countriesByCCA2 = new HashMap<>();
    private static final HashMap<String, Country> countriesByCCA3 = new HashMap<>();
    private static final HashMap<String, Country> countriesByOfficialName = new HashMap<>();
    private static final HashMap<String, Country> countriesByCommonName = new HashMap<>();
    private static final HashMap<Language, ArrayList<Country>> countriesByLanguages = new HashMap<>();
    private static final HashMap<Region, ArrayList<Country>> countriesByRegion = new HashMap<>();
    private static final HashMap<SubRegion, ArrayList<Country>> countriesBySubRegion = new HashMap<>();

    static {
        Language.load();
        Region.load();
        SubRegion.load();
        load();
    }

    public static Country getCountry(String country){
        Preconditions.checkNotNull(country, "country");

        if(countriesByCCA2.containsKey(country))
            return countriesByCCA2.get(country);
        if(countriesByCCA3.containsKey(country))
            return countriesByCCA3.get(country);
        if(countriesByOfficialName.containsKey(country))
            return countriesByOfficialName.get(country);
        if(countriesByCommonName.containsKey(country))
            return countriesByCommonName.get(country);
        else
            return null;
    }

    public static List<Country> getCountries(){
        return new ArrayList<>(countries);
    }

    public static List<Country> getCountriesByLanguage(Language language){
        Preconditions.checkNotNull(language, "language");

        return new ArrayList<>(countriesByLanguages.get(language));
    }

    public static List<Country> getCountriesByRegion(Region region){
        Preconditions.checkNotNull(region, "region");

        return new ArrayList<>(countriesByRegion.get(region));
    }

    public static List<Country> getCountriesBySubRegion(SubRegion subRegion){
        Preconditions.checkNotNull(subRegion, "subRegion");

        return new ArrayList<>(countriesBySubRegion.get(subRegion));
    }

    public static void load(){
        if(fileRead)
            return;

        JsonArray jsonArray = ResourceUtil.loadJsonArray("countries.json");
        fileRead = true;

        for(int i = 0; i < jsonArray.size(); i++){
            Country country = new Country(jsonArray.get(i).getAsJsonObject());

            countries.add(country);
            countriesByCCA2.put(country.getCCA2(), country);
            countriesByCCA3.put(country.getCCA3(), country);
            countriesByOfficialName.put(country.getOfficialName(), country);
            countriesByCommonName.put(country.getCommonName(), country);

            if(!countriesByRegion.containsKey(country.getRegion()))
                countriesByRegion.put(country.getRegion(), new ArrayList<>());

            countriesByRegion.get(country.getRegion()).add(country);

            if(!countriesBySubRegion.containsKey(country.getSubRegion()))
                countriesBySubRegion.put(country.getSubRegion(), new ArrayList<>());

            countriesBySubRegion.get(country.getSubRegion()).add(country);

            for(Language language : country.getLanguages()){
                if(!countriesByLanguages.containsKey(language))
                    countriesByLanguages.put(language, new ArrayList<>());

                countriesByLanguages.get(language).add(country);
            }
        }
    }

    private final JsonObject jsonObject;
    private final List<String> callingCodes = new ArrayList<>();
    private final List<String> currencies = new ArrayList<>();
    private final List<String> tlds = new ArrayList<>();
    private final List<String> altSpellings = new ArrayList<>();
    private final List<String> borders = new ArrayList<>();
    private final List<Language> languages = new ArrayList<>();

    private Country(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        initList(currencies, jsonObject.getAsJsonArray("currency"));
        initList(tlds, jsonObject.getAsJsonArray("tld"));
        initList(callingCodes, jsonObject.getAsJsonArray("callingCode"));
        initList(altSpellings, jsonObject.getAsJsonArray("altSpellings"));
        initList(borders, jsonObject.getAsJsonArray("borders"));
        initLanguages();
    }

    /**
     * The superficy of the country in km2
     * @return
     */
    public int getAreaSuperficy(){
        return jsonObject.get("area").getAsInt();
    }

    public boolean isLandLocked(){
        return jsonObject.get("landlocked").getAsBoolean();
    }

    public String getDemonym(){
        return jsonObject.get("demonym").getAsString();
    }

    public double getLatitude(){
        return jsonObject.getAsJsonArray("latlng").get(0).getAsDouble();
    }

    public double getLongitude(){
        return jsonObject.getAsJsonArray("latlng").get(1).getAsDouble();
    }

    public List<Language> getLanguages(){
        return new ArrayList<>(languages);
    }

    public SubRegion getSubRegion(){
        return SubRegion.getSubRegion(jsonObject.get("subregion").getAsString());
    }

    public Region getRegion(){
        return Region.getRegion(jsonObject.get("region").getAsString());
    }

    public String getCapital(){
        return jsonObject.get("capital").getAsString();
    }

    public String getCommonName(){
        return jsonObject.get("name").getAsJsonObject().get("common").getAsString();
    }

    public String getOfficialName(){
        return jsonObject.get("name").getAsJsonObject().get("official").getAsString();
    }

    public String getNativeCommonName(Language language){
        return jsonObject.getAsJsonObject("name").getAsJsonObject("native").getAsJsonObject(language.getISO639_3()).get("common").getAsString();
    }

    public String getNativeOfficialName(Language language){
        return jsonObject.getAsJsonObject("name").getAsJsonObject("native").getAsJsonObject(language.getISO639_3()).get("official").getAsString();
    }

    public String getTranslationCommonName(Language language){
        return jsonObject.getAsJsonObject("translations").getAsJsonObject(language.getISO639_3()).get("common").getAsString();
    }

    public String getTranslationOfficialName(Language language){
        return jsonObject.getAsJsonObject("translations").getAsJsonObject(language.getISO639_3()).get("official").getAsString();
    }

    public String getCCA2(){
        return jsonObject.get("cca2").getAsString();
    }

    public String getCCA3(){
        return jsonObject.get("cca3").getAsString();
    }

    public String getCCN3(){
        return jsonObject.get("ccn3").getAsString();
    }

    public String getCIOC(){
        return jsonObject.get("cioc").getAsString();
    }

    public List<String> getCurrencies(){
        return new ArrayList<>(currencies);
    }

    public List<String> getTlds(){
        return new ArrayList<>(tlds);
    }

    public List<String> getCallingCodes(){
        return new ArrayList<>(callingCodes);
    }

    public List<String> getAltSpellings(){
        return new ArrayList<>(altSpellings);
    }

    public List<String> getBorders(){
        return new ArrayList<>(borders);
    }

    private void initLanguages(){
        for(Map.Entry<String,JsonElement> entry : jsonObject.getAsJsonObject("languages").entrySet())
            languages.add(Language.getLanguage(entry.getKey()));
    }

    private void initList(List<String> list, JsonArray array){
        for(int i = 0; i < array.size(); i++)
            list.add(array.get(i).getAsString());
    }

}

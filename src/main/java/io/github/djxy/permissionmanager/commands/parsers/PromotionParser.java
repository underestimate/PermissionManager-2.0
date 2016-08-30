package io.github.djxy.permissionmanager.commands.parsers;

import io.github.djxy.customcommands.parsers.Parser;
import io.github.djxy.permissionmanager.promotion.Promotion;
import io.github.djxy.permissionmanager.promotion.Promotions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-27.
 */
public class PromotionParser extends Parser<Promotion> {

    @Override
    public Promotion parse(String value) {
        return Promotions.instance.getPromotion(value);
    }

    @Override
    public List<String> getSuggestions(String value) {
        List<String> promotions = new ArrayList<>();

        value = value.toLowerCase();

        for(Promotion promotion : Promotions.instance.getPromotions())
            if(promotion.getName().toLowerCase().startsWith(value))
                promotions.add(promotion.getName());

        if(promotions.size() == 1 && promotions.get(0).equalsIgnoreCase(value))
            promotions.clear();

        return promotions;
    }

}

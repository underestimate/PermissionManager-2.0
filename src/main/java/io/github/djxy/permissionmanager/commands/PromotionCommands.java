package io.github.djxy.permissionmanager.commands;

import io.github.djxy.customcommands.annotations.CustomCommand;
import io.github.djxy.customcommands.annotations.CustomParser;
import io.github.djxy.permissionmanager.commands.parsers.PromotionParser;
import io.github.djxy.permissionmanager.commands.parsers.UserParser;
import io.github.djxy.permissionmanager.exceptions.PromotionNameExistException;
import io.github.djxy.permissionmanager.promotion.Promotion;
import io.github.djxy.permissionmanager.promotion.Promotions;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.action.TextActions;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Samuel on 2016-08-27.
 */
public class PromotionCommands extends Command {

    private static final String PERMISSION_PROMOTION_RENAME = "permissionmanager.commands.promotions.rename";
    private static final String PERMISSION_PROMOTION_DELETE = "permissionmanager.commands.promotions.delete";
    private static final String PERMISSION_PROMOTION_CREATE = "permissionmanager.commands.promotions.create";
    private static final String PERMISSION_PROMOTION_PROMOTE = "permissionmanager.commands.promotions.promote";
    private static final String PERMISSION_PROMOTION_LOAD = "permissionmanager.commands.promotions.load";
    private static final String PERMISSION_PROMOTION_SAVE = "permissionmanager.commands.promotions.save";

    public PromotionCommands(Translator translator) {
        super(translator);
    }

    @CustomCommand(
            command = "pm promote #user #promotion",
            permission = PERMISSION_PROMOTION_PROMOTE,
            parsers = {
                    @CustomParser(argument = "user", parser = UserParser.class),
                    @CustomParser(argument = "promotion", parser = PromotionParser.class)
            }
    )
    public void promoteUser(CommandSource source, Map<String, Object> values) {
        User user = (User) values.get("user");
        Promotion promotion = (Promotion) values.get("promotion");

        promotion.promote(user);

        source.sendMessage(
                parser.parse(
                        translator.getTranslation(getLanguage(source), "promote_user"),
                        EMPTY_MAP,
                        createVariableMap(
                                "user", Sponge.getServer().getGameProfileManager().getCache().getById(user.getUniqueId()).get().getName().get(),
                                "promotion", promotion.getName()
                        ),
                        EMPTY_MAP
                )
        );
    }

    @CustomCommand(
            command = "pm load promotions",
            permission = PERMISSION_PROMOTION_LOAD,
            parsers = {}
    )
    public void loadPromotions(CommandSource source, Map<String, Object> values) {
        Promotions.instance.load();

        source.sendMessage(parser.parse(translator.getTranslation(getLanguage(source), "promotion_loaded_all"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP));
    }

    @CustomCommand(
            command = "pm load promotions #promotion",
            permission = PERMISSION_PROMOTION_LOAD,
            parsers = {@CustomParser(argument = "promotion", parser = PromotionParser.class)}
    )
    public void loadPromotion(CommandSource source, Map<String, Object> values) {
        Promotion promotion = (Promotion) values.get("promotion");

        if(Promotions.instance.load(promotion.getName())){
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "promotion_loaded"),
                            EMPTY_MAP,
                            createVariableMap("promotion", promotion.getName()),
                            EMPTY_MAP
                    )
            );
        }
        else
            parser.parse(translator.getTranslation(getLanguage(source), "loading_error"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP);
    }

    @CustomCommand(
            command = "pm save promotions",
            permission = PERMISSION_PROMOTION_SAVE,
            parsers = {}
    )
    public void savePromotions(CommandSource source, Map<String, Object> values) {
        Promotions.instance.save();

        source.sendMessage(parser.parse(translator.getTranslation(getLanguage(source), "promotion_saved_all"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP));
    }

    @CustomCommand(
            command = "pm save promotions #promotion",
            permission = PERMISSION_PROMOTION_SAVE,
            parsers = {@CustomParser(argument = "promotion", parser = PromotionParser.class)}
    )
    public void savePromotion(CommandSource source, Map<String, Object> values) {
        Promotion promotion = (Promotion) values.get("promotion");

        try {
            Promotions.instance.save(promotion.getName());

            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "promotion_saved"),
                            EMPTY_MAP,
                            createVariableMap("promotion", promotion.getName()),
                            EMPTY_MAP
                    )
            );
        } catch (Exception e) {
            parser.parse(translator.getTranslation(getLanguage(source), "saving_error"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP);
            e.printStackTrace();
        }
    }

    @CustomCommand(
            command = "pm rename promotion #promotion #newName",
            permission = PERMISSION_PROMOTION_RENAME,
            parsers = {@CustomParser(argument = "promotion", parser = PromotionParser.class)}
    )
    public void renamePromotion(CommandSource source, Map<String, Object> values) {
        Promotion promotion = (Promotion) values.get("promotion");

        try {
            String lastName = promotion.getName();

            Promotions.instance.renamePromotion(promotion, values.get("newName").toString());
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "promotion_rename_succesfully"),
                            EMPTY_MAP,
                            createVariableMap(
                                    "promotion", lastName,
                                    "newName", values.get("newName").toString()
                            ),
                            EMPTY_MAP
                    )
            );
        } catch (PromotionNameExistException e) {
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "promotion_rename_error_name_exist"),
                            EMPTY_MAP,
                            createVariableMap("newName", values.get("newName").toString()),
                            EMPTY_MAP
                    )
            );
        }
    }

    @CustomCommand(
            command = "pm create promotion #name",
            permission = PERMISSION_PROMOTION_CREATE,
            parsers = {}
    )
    public void createPromotion(CommandSource source, Map<String, Object> values) {
        try {
            Promotion promotion = Promotions.instance.createPromotion(values.get("name").toString());
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "promotion_create_succesfully"),
                            EMPTY_MAP,
                            createVariableMap("promotion", values.get("name").toString()),
                            EMPTY_MAP
                    )
            );

            try {
                Promotions.instance.save(promotion.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }   catch (PromotionNameExistException e) {
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "promotion_create_error_name_exist"),
                            EMPTY_MAP,
                            createVariableMap("promotion", values.get("name").toString()),
                            EMPTY_MAP
                    )
            );
        }
    }

    @CustomCommand(
            command = "pm delete promotion #promotion",
            permission = PERMISSION_PROMOTION_DELETE,
            parsers = {@CustomParser(argument = "promotion", parser = PromotionParser.class)}
    )
    public void deletePromotion(CommandSource source, Map<String, Object> values) {
        Promotion promotion = (Promotion) values.get("promotion");

        source.sendMessage(
                parser.parse(
                        translator.getTranslation(getLanguage(source), "promotion_delete_confirmation"),
                        createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                        createVariableMap("promotion", promotion.getName()),
                        createVariableMap("click_confirmation", TextActions.executeCallback(source1 -> {
                            Promotions.instance.deletePromotion(promotion.getName());

                            source.sendMessage(
                                    parser.parse(
                                            translator.getTranslation(getLanguage(source), "promotion_delete"),
                                            EMPTY_MAP,
                                            createVariableMap("promotion", promotion.getName()),
                                            EMPTY_MAP
                                    )
                            );

                        }))
                )
        );
    }

}

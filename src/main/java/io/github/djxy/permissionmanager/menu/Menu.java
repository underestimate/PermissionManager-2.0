package io.github.djxy.permissionmanager.menu;

import io.github.djxy.permissionmanager.menu.menus.MenuList;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-25.
 */
public abstract class Menu {

    private static URL WIKI_URL;
    private static URL ISSUE_URL;
    private static URL GITHUB_URL;

    private static final int MAX_NB_LINES = 17;

    static {
        try {
            WIKI_URL = new URL("https://github.com/djxy/PermissionManager-2.0/wiki");
            ISSUE_URL = new URL("https://github.com/djxy/PermissionManager-2.0/issues");
            GITHUB_URL = new URL("https://github.com/djxy/PermissionManager-2.0");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected final Menu from;
    protected Text title = null;
    protected Text titleCompiled = Text.of();
    protected final Player player;
    protected final Translator translator;

    abstract public void render(List<Text> lines);

    public Menu(Player player, Translator translator) {
        this(player, translator, null);
    }

    public Menu(Player player, Translator translator, Menu from) {
        this.player = player;
        this.translator = translator;
        this.from = from;
    }

    public final void sendToPlayer(){
        List<Text> lines = new ArrayList<>();

        render(lines);

        if(lines.size() % MAX_NB_LINES != 0 || lines.size() == 0) {
            int nbLine = MAX_NB_LINES - (lines.size() % MAX_NB_LINES);

            nbLine = from == null?nbLine+1:nbLine;

            boolean addLinks = nbLine >= 3;

            nbLine = addLinks?nbLine-2:nbLine;

            for (int i = 0; i < nbLine; i++)
                lines.add(Text.of(" "));

            if(addLinks) {
                lines.add(Text.of("───────────────────────────────────"));
                lines.add(createUrl("Wiki", WIKI_URL).concat(Text.of(TextColors.WHITE, " | ")).concat(createUrl("Github", GITHUB_URL)).concat(Text.of(TextColors.WHITE, " | ")).concat(createUrl("Report an issue", ISSUE_URL)));
            }
        }

        PaginationList.Builder builder = Sponge.getServiceManager().provide(PaginationService.class).get().builder()
                .contents(lines)
                .padding(Text.of("─"));

        if(from != null) {
            builder.footer(Text.of(TextActions.executeCallback(source -> {
                from.sendToPlayer();
            }),
                            TextStyles.UNDERLINE,
                            TextColors.RED,
                            "« "+translator.getTranslation(UserCollection.instance.get(player).getLanguage(), "menu_back"),
                            TextStyles.RESET,
                            TextColors.WHITE,
                            " | ",
                            TextStyles.UNDERLINE,
                            TextColors.BLUE,
                            TextActions.executeCallback(source -> {
                                sendToPlayer();
                            }),
                            translator.getTranslation(UserCollection.instance.get(player).getLanguage(), "menu_refresh")
                    )
            );

            titleCompiled = from.titleCompiled;

            if(title != null)
                titleCompiled = titleCompiled.concat(Text.of(" / ")).concat(title);
        }
        else if(title != null)
            titleCompiled = title;

        builder
                .title(titleCompiled)
                .build()
                .sendTo(player);
    }

    protected Text createTitle(String title){
        return Text.of(TextColors.GOLD, TextActions.executeCallback(source -> {this.sendToPlayer();}), title);
    }

    protected Text createOption(TextAction action, String title){
        return Text.of("- ", TextColors.GREEN, action, title);
    }

    protected Text createUrl(String name, URL url){
        return Text.of(TextColors.GOLD, TextActions.openUrl(url), name);
    }

    protected TextAction runCommandAndRefreshMenuTextAction(String command){
        return TextActions.executeCallback(source -> {
            Sponge.getCommandManager().process(source, command);
            sendToPlayer();
        });
    }

    protected void runCommandAndRefreshMenu(String command){
        Sponge.getCommandManager().process(player, command);
        sendToPlayer();
    }

    protected TextAction suggestCommand(String command){
        return TextActions.suggestCommand("/" + command);
    }

    protected TextAction goToMenuTextAction(Class<? extends Menu> menu){
        Menu from = this;

        return TextActions.executeCallback(source -> {
            try {
                menu.getConstructor(Player.class, Translator.class, Menu.class).newInstance(player, translator, from).sendToPlayer();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    protected TextAction goToMenuListTextAction(Class<? extends MenuList> menuList, Consumer<String> callback){
        return TextActions.executeCallback(source -> {
            try {
                menuList.getConstructor(Player.class, Translator.class, Menu.class, Consumer.class).newInstance(player, translator, this, callback).sendToPlayer();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    protected TextAction goToMenuListTextAction(Class<? extends MenuList> menuList, String commandSuggested){
        return TextActions.executeCallback(source -> {
            try {
                menuList.getConstructor(Player.class, Translator.class, Menu.class, String.class).newInstance(player, translator, this, commandSuggested).sendToPlayer();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

}
